package com.beesphere.edi.reader.impls;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.beesphere.edi.OutputHandler;
import com.beesphere.edi.model.Composite;
import com.beesphere.edi.model.Container;
import com.beesphere.edi.model.Field;
import com.beesphere.edi.model.Group;
import com.beesphere.edi.model.Model;
import com.beesphere.edi.model.Segment;
import com.beesphere.edi.model.ValueElement;
import com.beesphere.edi.reader.Reader;
import com.beesphere.edi.reader.ReaderException;
import com.beesphere.edi.reader.Tokenizer;
import com.qlogic.commons.utils.strings.StringUtils;

public class DefaultReader implements Reader {
	
	private static final long serialVersionUID = 8894780992011672296L;

	private static final Logger logger = LoggerFactory.getLogger (DefaultReader.class);   
	
	private static Pattern EMPTY_LINE = Pattern.compile("[\n\r ]*");

	private OutputHandler outputHandler;
	private Model model;

	private Tokenizer tokenizer;

	public DefaultReader () {
	}
	
	public DefaultReader (Tokenizer tokenizer) {
		this.tokenizer = tokenizer;
	}
	
	@Override
	public void read (InputStream in, OutputHandler outputHandler) throws ReaderException {
		this.outputHandler = outputHandler;
		if (outputHandler == null) {
			throw new IllegalStateException(
					"'outputHandler' not set.  Cannot parse EDI stream.");
		}

		if (this.model == null) {
			throw new IllegalStateException(
					"Model not set.  Cannot parse EDI stream.");
		}
		
		if (this.model.getRoot () == null) {
			throw new IllegalStateException(
					"Model's root group not set.  Cannot parse EDI stream.");
		}
		
		if (tokenizer == null) {
			tokenizer = new DialectBasedTokenizer ();
		}
		if (!tokenizer.isInitialized ()) {
			tokenizer.init (in, model);
		}

		if (logger.isDebugEnabled()) {
            logger.debug ("Start Reading Edi Stream (" + model.getRoot ().getName () + ") using " + 
            					tokenizer.getDialect ().getClass().getSimpleName () + ": " + tokenizer.getDialect ());
        }
		
		// Fire the startDocument event, as well as the startElement event...
		try {
			outputHandler.onStart (null, OutputHandler.Kind.DOCUMENT);
			outputHandler.onStart (model.getRoot (), OutputHandler.Kind.GROUP);
		} catch (IOException e) {
			throw new ReaderException (e);
		}

		// Work through all the segments in the model. Move to the first segment
		// before starting...
		String segment = tokenizer.next ();
		if (segment != null) {
			mapContainers (model.getRoot ().getName (), model.getRoot ().getContainers ());
			// If we reach the end of the mapping model and we still have more
			// EDI segments in the message....
			while ((segment = tokenizer.next ()) != null) {
				if (!EMPTY_LINE.matcher(segment).matches()) {
					throw new ReaderException (model.toString () + " --> " +
							"Reached end of mapping model but there are more EDI segments in the incoming message.  Read "
									+ tokenizer.index ()
									+ " segment(s). Current EDI segment is ["
									+ segment + "]");
				}
			}
			logger.debug ("End");
		}

		// Fire the endDocument event, as well as the endElement event...
		try {
			outputHandler.onEnd (model.getRoot (), OutputHandler.Kind.GROUP);
			outputHandler.onEnd (null, OutputHandler.Kind.DOCUMENT);
		} catch (IOException e) {
			throw new ReaderException (e);
		}
	}

	/**
	 * Map a list of EDI Segments to SAX events. <p/> Reads the segments from
	 * the input stream and maps them based on the supplied list of expected
	 * segments.
	 * 
	 * @param expectedSegments
	 *            The list of expected segments.
	 * @throws IOException
	 *             Error reading an EDI segment from the input stream.
	 * @throws SAXException
	 *             EDI processing exception.
	 */
	private void mapContainers (String parent, List<Container> containers)
			throws ReaderException {
		mapContainers (parent, containers, null);
	}

	/**
	 * Map a list of EDI Segments to SAX events. <p/> Reads the segments from
	 * the input stream and maps them based on the supplied list of expected
	 * segments.
	 * 
	 * @param expectedSegments
	 *            The list of expected segments.
	 * @param preLoadedSegmentFields
	 *            Preloaded segment. This can happen in the case of a
	 *            segmentGroup.
	 * @throws IOException
	 *             Error reading an EDI segment from the input stream.
	 * @throws SAXException
	 *             EDI processing exception.
	 */
	private void mapContainers (String parent, List<Container> expectedSegments,
			String [] preLoadedSegmentFields) throws ReaderException {
		int segmentMappingIndex = 0; // The current index within the supplied
										// segment list.
		int segmentProcessingCount = 0; // The number of times the current
										// segment definition from the supplied
										// segment list has been applied to
										// message segments on the incomming EDI
										// message.
		
		String [] fields = preLoadedSegmentFields;

		if (expectedSegments == null || expectedSegments.size() == 0) {
			return;
		}

		while (segmentMappingIndex < expectedSegments.size() && tokenizer.curr () != null && tokenizer.curr ().length () > 0) {
			Container container = expectedSegments.get (segmentMappingIndex);
			int minOccurs = container.getMinOccurs();
			int maxOccurs = container.getMaxOccurs();

			// A negative max value indicates an unbound max....
			if (maxOccurs < 0) {
				maxOccurs = Integer.MAX_VALUE;
			}
			// Make sure min is not greater than max...
			if (minOccurs > maxOccurs) {
				maxOccurs = minOccurs;
			}

			// Only load the next segment if currentSegmentFields == null i.e.
			// we don't have a set of
			// preLoadedSegmentFields (see method args) that need to be
			// processed first...
			if (fields == null) {
				fields = tokenizer.fields ();
			}
			
			Segment segment = null;
			if (Group.class.isAssignableFrom (container.getClass ())) {
				segment = getFirstSegment ((Group)container);
			} else {
				segment = (Segment)container;
			}
			
			// If the current segment being read from the incoming message
			// doesn't match the expected
			// segment name....
			if (segment.getPattern () != null && !fields [0].equals (segment.getName ())) {
				Matcher matcher = segment.getPattern ().matcher (tokenizer.curr ());
				if (!matcher.matches()) {
					// If we haven't read the minimum number of instances of the
					// current "expected" segment, raise an error...
					if (segmentProcessingCount < minOccurs) {
						throw new ReaderException (model.toString () + " --> " +
								"Must be a minimum of "
										+ minOccurs
										+ " instances of segment ["
										+ segment.getName ()
										+ "], Data[" + tokenizer.curr () + "].  Currently at segment number "
										+ tokenizer.index ()
										+ ".");
					} else {
						// Otherwise, move to the next "expected" segment and
						// start the loop again...
						segmentMappingIndex++;
						segmentProcessingCount = 0;
						continue;
					}
				}
			}

			// Make sure we haven't encountered a message with too many
			// instances of the current expected segment...
			if (segmentProcessingCount >= maxOccurs) {
				throw new ReaderException (model.toString () + " --> " +
								"Maximum of " + maxOccurs + " instances of segment ["
								+ segment.getName ()
								+ "], Data[" + tokenizer.curr () + "] exceeded.  Currently at segment number "
								+ tokenizer.index () + ".");
			}

			// The current read message segment appears to match that expected
			// according to the mapping model.
			// Proceed to process the segment fields and the segments
			// sub-segments...
			if (Segment.class.isAssignableFrom (container.getClass ())) {
				mapSegment (fields, (Segment) container);
			} else {
				if (container.getName () != null) {
					try {
						outputHandler.onStart (container, OutputHandler.Kind.GROUP);
					} catch (IOException e) {
						throw new ReaderException (e);
					}
				}
				mapContainers (container.getName (), ((Group)container).getContainers (), fields);
				
				if (container.getName () != null) {
					try {
						outputHandler.onEnd (container, OutputHandler.Kind.GROUP);
					} catch (IOException e) {
						throw new ReaderException (e);
					}
				}
			}

			// Increment the count on the number of times the current "expected"
			// mapping config has been applied...
			segmentProcessingCount++;
			fields = null;

			if (segmentProcessingCount < minOccurs && (tokenizer.curr () == null || tokenizer.curr ().length () <= 0)) {
				throw new ReaderException (model.toString () + " --> " +
								"Reached end of EDI message stream but there must be a minimum of "
								+ minOccurs + " instances of segment ["
								+ segment.getName ()
								+ "].  Currently at segment number "
								+ tokenizer.index () + ".");
			}
		}
	}
	
	private Segment getFirstSegment (Group group) {
		Container segmentOrGroup = group.getContainers().get (0);
		if (Segment.class.isAssignableFrom (segmentOrGroup.getClass ())) {
			return (Segment)segmentOrGroup;
		}
		return getFirstSegment ((Group)segmentOrGroup);
	}

	/**
	 * Map a single segment based on the current set of segment fields read from
	 * input and the segment mapping config that these fields should map to.
	 * 
	 * @param currentSegmentFields
	 *            Current set of segment fields read from input.
	 * @param expectedSegment
	 *            The segment mapping config that the currentSegmentFields
	 *            should map to.
	 * @throws IOException
	 *             Error reading an EDI segment from the input stream. This will
	 *             happen as the segment reader tries to move to the next
	 *             segment after performing this mapping.
	 * @throws SAXException
	 *             EDI processing exception.
	 */
	private void mapSegment (String [] currentSegmentFields, Segment segment) throws ReaderException {
		try {
			outputHandler.onStart (segment, OutputHandler.Kind.SEGMENT);
			mapFields (currentSegmentFields, segment);
			outputHandler.onEnd (segment, OutputHandler.Kind.SEGMENT);
		} catch (IOException e) {
			throw new ReaderException (e);
		}
		tokenizer.next ();
	}

	/**
	 * Map the individual field values based on the supplied expected field
	 * configs.
	 * 
	 * @param currentSegmentFields
	 *            Segment fields from the input message.
	 * @param segment
	 *            List of expected field mapping configurations that the
	 *            currentSegmentFields are expected to map to.
	 * @throws SAXException
	 *             EDI processing exception.
	 */
	private void mapFields (String [] currentSegmentFields, Segment segment)
			throws ReaderException {
		
		List<ValueElement> expectedFields = segment.getElements ();
		
		// Make sure all required fields are present in the incoming message...
		assertFieldsOK (currentSegmentFields, segment);
		
		// Iterate over the fields and map them...
		int numFields = currentSegmentFields.length - 1; // It's
															// "currentSegmentFields.length
															// - 1" because we
															// don't want to
															// include the
															// segment code.

		for (int i = 0; i < numFields; i++) {
			String fieldMessageVal = currentSegmentFields[i + 1]; // +1 to
																	// skip the
																	// segment
																	// code
			ValueElement expectedField = expectedFields.get (i);
			mapField (currentSegmentFields, fieldMessageVal, expectedField, i, segment.getName ());
		}
	}

	/**
	 * Map an individual segment field.
	 * 
	 * @param fieldMessageVal
	 *            The field message value.
	 * @param expectedField
	 *            The mapping config to which the field value is expected to
	 *            map.
	 * @param fieldIndex
	 *            The field index within its segment (base 0).
	 * @param segmentCode
	 *            The segment code within which the field exists.
	 * @throws SAXException
	 *             EDI processing exception.
	 */
	private void mapField (String [] segLine, String fieldMessageVal, ValueElement element,
			int fieldIndex, String segmentName) throws ReaderException {
		
		if (Composite.class.isAssignableFrom (element.getClass ())) {
			String[] currentFieldComponents = EDIUtils.split(fieldMessageVal,
					tokenizer.getDialect ().getField(), tokenizer.getDialect ().getEscape());
			
			List<ValueElement> fieldsOrSubComposites = ((Composite)element).getElements ();

			assertComponentsOK (element, fieldIndex, segmentName, fieldsOrSubComposites, currentFieldComponents);

			if (currentFieldComponents.length > 0) {
				try {
					outputHandler.onStart (element, OutputHandler.Kind.COMPOSITE);
				} catch (IOException e) {
					throw new ReaderException (e);
				}
				// Iterate over the field components and map them...
				for (int i = 0; i < currentFieldComponents.length; i++) {
					ValueElement fieldOrSubComposite = fieldsOrSubComposites.get (i);
					mapFieldOrSubComposite (fieldMessageVal, currentFieldComponents [i], fieldOrSubComposite,
							fieldIndex, i, segmentName, element.getName ());
				}
				try {
					outputHandler.onEnd (element, OutputHandler.Kind.COMPOSITE);
				} catch (IOException e) {
					throw new ReaderException (e);
				}
			}
			
		} else {
			if (element.getMinOccurs () > 0 && fieldMessageVal.length() == 0) {
				throw new ReaderException (model.toString () + " --> " +
								"Segment ["
								+ segmentName
								+ "], Data[" + StringUtils.arrayToString (segLine, ", ") + "], field "
								+ (fieldIndex + 1)
								+ " ("
								+ element.getName ()
								+ ") expected to contain a value.  Currently at segment number "
								+ tokenizer.index() + ".");
			}
			try {
				outputHandler.onData (element, OutputHandler.Kind.FIELD, fieldMessageVal);
			} catch (IOException e) {
				throw new ReaderException (e);
			}
		}
		
	}

	/**
	 * Map an individual component.
	 * 
	 * @param componentMessageVal
	 *            Component message value read from EDI input.
	 * @param expectedComponent
	 *            The mapping config to which the component value is expected to
	 *            map.
	 * @param fieldIndex
	 *            The field index within its segment (base 0) in which the
	 *            component exists.
	 * @param componentIndex
	 *            The component index within its field (base 0).
	 * @param segmentCode
	 *            The segment code within which the component exists.
	 * @param field
	 *            Field within which the component exists.
	 * @throws SAXException
	 *             EDI processing exception.
	 */
	private void mapFieldOrSubComposite (String parentText, String componentMessageVal,
			ValueElement valueElement, int fieldIndex, int componentIndex,
			String segmentName, String field) throws ReaderException {
		
		if (Composite.class.isAssignableFrom (valueElement.getClass ())) {
			String[] currentComponentSubComponents = EDIUtils.split(
					componentMessageVal, tokenizer.getDialect ()
							.getSubField (), tokenizer.getDialect ()
							.getEscape());

			List<ValueElement> subCompositeElements = ((Composite)valueElement).getElements ();

			assertSubComponentsOK (valueElement, fieldIndex,
					componentIndex, segmentName, field, subCompositeElements,
					currentComponentSubComponents);
			
			try {
				outputHandler.onStart (valueElement, OutputHandler.Kind.COMPOSITE);
			} catch (IOException e) {
				throw new ReaderException (e);
			}
			
			for (int i = 0; i < currentComponentSubComponents.length; i++) {
				if (subCompositeElements.get(i).getMinOccurs () > 0 && currentComponentSubComponents[i].length() == 0) {
					throw new ReaderException(model.toString () + " --> " +
									"Segment ["
									+ segmentName
									+ "], Data[" + parentText + "], Composite "
									+ (fieldIndex + 1)
									+ " ("
									+ field
									+ "), SubComposite "
									+ (componentIndex + 1)
									+ " ("
									+ valueElement.getName()
									+ "), Field "
									+ (i + 1)
									+ " ("
									+ subCompositeElements.get(i).getName ()
									+ ") expected to contain a value.  Currently at segment number "
									+ tokenizer.index ()
									+ ".");
				}

				try {
					outputHandler.onData (subCompositeElements.get(i), OutputHandler.Kind.FIELD, currentComponentSubComponents[i]);
				} catch (IOException e) {
					throw new ReaderException (e);
				}
			}
			try {
				outputHandler.onEnd (valueElement, OutputHandler.Kind.COMPOSITE);
			} catch (IOException e) {
				throw new ReaderException (e);
			}

		} else if (Field.class.isAssignableFrom (valueElement.getClass ())) {
			if (valueElement.getMinOccurs () > 0 && componentMessageVal.length() == 0) {
				throw new ReaderException (model.toString () + " --> " +
								"Segment ["
								+ segmentName
								+ "], Data[" + parentText + "], field "
								+ (fieldIndex + 1)
								+ " ("
								+ field
								+ "), component "
								+ (componentIndex + 1)
								+ " ("
								+ valueElement.getName ()
								+ ") expected to contain a value.  Currently at segment number "
								+ tokenizer.index () + ".");
			}
			try {
				outputHandler.onData (valueElement, OutputHandler.Kind.FIELD, componentMessageVal);
			} catch (IOException e) {
				throw new ReaderException (e);
			}
		}
	}

	private void assertFieldsOK (String[] currentSegmentFields, Segment segment)
			throws ReaderException {

		List<ValueElement> expectedFields = segment.getElements ();

		int numFieldsExpected = expectedFields.size() + 1; // It's
															// "expectedFields.length
															// + 1" because the
															// segment code is
															// included.

		if (currentSegmentFields.length > numFieldsExpected) {
			throw new ReaderException (model.toString () + " --> " +
					"Segment ["
					+ segment.getName ()
					+ "], Data[" + StringUtils.arrayToString (currentSegmentFields, ", ") + "] expected to contain "
					+ (numFieldsExpected - 1)
					+ " composites/fields.  Actually contains "
					+ (currentSegmentFields.length - 1)
					+ " composites/fields (not including segment code).  Currently at segment number "
					+ tokenizer.index () + ".");
		}
		
		if (currentSegmentFields.length != numFieldsExpected) {
			boolean throwException = false;

			// If we don't have all the fields we're expecting, check is the
			// Segment truncatable
			// and are the missing fields required or not...
			if (segment.isTruncatable()) {
				int numFieldsMissing = numFieldsExpected
						- currentSegmentFields.length;
				for (int i = expectedFields.size() - 1; 
						i > (expectedFields.size() - numFieldsMissing - 1); 
							i--) {
					if (expectedFields.get(i).getMinOccurs () > 0) {
						throwException = true;
						break;
					}
				}
			} else {
				throwException = true;
			}

			if (throwException) {
				throw new ReaderException (model.toString () + " --> " +
								"Segment ["
								+ segment.getName ()
								+ "], Data[" + StringUtils.arrayToString (currentSegmentFields, ", ") + "] expected to contain "
								+ (numFieldsExpected - 1)
								+ " fields.  Actually contains "
								+ (currentSegmentFields.length - 1)
								+ " fields (not including segment code).  Currently at segment number "
								+ tokenizer.index () + ".");
			}
		}
	}

	private void assertComponentsOK (ValueElement field, int fieldIndex,
			String segmentName, List<ValueElement> expectedComponents,
			String[] currentFieldComponents) throws ReaderException {

		if (currentFieldComponents.length > expectedComponents.size()) {
			throw new ReaderException (model.toString () + " --> " +
					"Segment [" + segmentName + "], Data[" + StringUtils.arrayToString (currentFieldComponents, ", ") + "], Composite ("
					+ field.getName ()
					+ ") expected to contain "
					+ expectedComponents.size()
					+ " components.  Actually contains "
					+ currentFieldComponents.length
					+ " components.  Currently at segment number "
					+ tokenizer.index () + ".");
		}
		
		if (currentFieldComponents.length != expectedComponents.size()) {
			boolean throwException = false;

			if (field.isTruncatable()) {

				// B�rd: When there are no Components in Field it should not
				// throw exception, since
				// the Field is just created (with Field-separator) for
				// satisfying requirement for Fields
				// that are required later in Segment.
				if (currentFieldComponents.length == 0) {
					return;
				}

				int numComponentsMissing = expectedComponents.size()
						- currentFieldComponents.length;
				for (int i = expectedComponents.size() - 1; i > (expectedComponents.size()
						- numComponentsMissing - 1); i--) {
					if (expectedComponents.get(i).getMinOccurs () > 0) {
						throwException = true;
						break;
					}
				}
			} else {
				throwException = true;
			}

			if (throwException) {
				throw new ReaderException (model.toString () + " --> " +
								"Segment [" + segmentName + "], Data[" + StringUtils.arrayToString (currentFieldComponents, ", ") + "], Composite ("
								+ field.getName ()
								+ ") expected to contain "
								+ expectedComponents.size()
								+ " components.  Actually contains "
								+ currentFieldComponents.length
								+ " components.  Currently at segment number "
								+ tokenizer.index () + ".");
			}
		}
	}

	private void assertSubComponentsOK (ValueElement expectedComponent,
			int fieldIndex, int componentIndex, String segmentCode,
			String field, List<ValueElement> expectedSubComponents,
			String[] currentComponentSubComponents) throws ReaderException {
		if (currentComponentSubComponents.length != expectedSubComponents
				.size()) {
			boolean throwException = false;

			if (expectedComponent.isTruncatable()) {

				// B�rd: When there are no SubComponents in field it should
				// not throw exception, since
				// the Component is just created (with Component-separator) for
				// satisfying requirement
				// for Components that are required later in Field.
				if (currentComponentSubComponents.length == 0) {
					return;
				}

				int numSubComponentsMissing = expectedSubComponents.size()
						- currentComponentSubComponents.length;
				for (int i = expectedSubComponents.size() - 1; i > (expectedSubComponents
						.size()
						- numSubComponentsMissing - 1); i--) {
					if (expectedSubComponents.get(i).getMinOccurs () > 0) {
						throwException = true;
						break;
					}
				}
			} else {
				throwException = true;
			}

			if (throwException) {
				throw new ReaderException (model.toString () + " --> " +
								"Segment ["
								+ segmentCode
								+ "], Data[" + StringUtils.arrayToString (currentComponentSubComponents, ", ") + "], composite/field "
								+ (fieldIndex + 1)
								+ " ("
								+ field
								+ "), component "
								+ (componentIndex + 1)
								+ " ("
								+ expectedComponent.getName ()
								+ ") expected to contain "
								+ expectedSubComponents.size()
								+ " sub-components.  Actually contains "
								+ currentComponentSubComponents.length
								+ " sub-components.  Currently at segment number "
								+ tokenizer.index () + ".");
			}
		}
	}

	public Tokenizer getTokenizer() {
		return tokenizer;
	}

	public void setTokenizer(Tokenizer tokenizer) {
		this.tokenizer = tokenizer;
	}

	public Model getModel () {
		return model;
	}

	public Reader setModel (Model model) {
		this.model = model;
		return this;
	}
}
