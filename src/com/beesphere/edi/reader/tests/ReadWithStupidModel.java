package com.beesphere.edi.reader.tests;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.beesphere.edi.model.Container;
import com.beesphere.edi.model.ValueElement;
import com.beesphere.edi.model.impl.basic.CompositeImpl;
import com.beesphere.edi.model.impl.basic.FieldImpl;
import com.beesphere.edi.model.impl.basic.GroupImpl;
import com.beesphere.edi.model.impl.basic.ModelImpl;
import com.beesphere.edi.model.impl.basic.SegmentImpl;
import com.beesphere.edi.reader.ReaderException;
import com.beesphere.edi.reader.impls.ReaderOutputHandler;
import com.beesphere.edi.reader.impls.DefaultReader;
import com.beesphere.edi.reader.impls.DefaultTokenizer;

public class ReadWithStupidModel {

	/**
	 * 
	<medi:segments xmltag="Order">

		<medi:segment segcode="HDR" xmltag="header">
			<medi:field xmltag="order-id" />
			<medi:field xmltag="status-code" />
			<medi:field xmltag="net-amount" />
			<medi:field xmltag="total-amount" />
			<medi:field xmltag="tax" />
			<medi:field xmltag="date" />
		</medi:segment>

		<medi:segment segcode="CUS" xmltag="customer-details">
			<medi:field xmltag="username" />
			<medi:field xmltag="name">
				<medi:component xmltag="firstname" />
				<medi:component xmltag="lastname" />
			</medi:field>
			<medi:field xmltag="state" />
		</medi:segment>

		<medi:segment segcode="ORD" xmltag="order-item" maxOccurs="-1">
			<medi:field xmltag="position" />
			<medi:field xmltag="quantity" />
			<medi:field xmltag="product-id" />
			<medi:field xmltag="title" />
			<medi:field xmltag="price" />
		</medi:segment>

	</medi:segments>
	 * @throws ReaderException 
	 * @throws IOException 
	 * 
	 */
	public static void main(String[] args) throws ReaderException, IOException {
		GroupImpl root = new GroupImpl ();
		root.setName ("Order");

		// HDR Segment
		SegmentImpl hdr = new SegmentImpl ("HDR");
		hdr.setMaxOccurs (-1);
		List<ValueElement> hdrElements = new ArrayList <ValueElement> ();
		hdrElements.add (new FieldImpl ("order-id"));
		hdrElements.add (new FieldImpl ("status-code"));
		hdrElements.add (new FieldImpl ("net-amount"));
		hdrElements.add (new FieldImpl ("total-amount"));
		hdrElements.add (new FieldImpl ("tax"));
		hdrElements.add (new FieldImpl ("date"));
		hdr.setElements (hdrElements);
		
		// CUS Segment
		SegmentImpl cus = new SegmentImpl ("CUS");
		cus.setMaxOccurs (-1);
		List<ValueElement> cusElements = new ArrayList <ValueElement> ();
		
		cusElements.add (new FieldImpl ("username"));
		
		CompositeImpl nameComposite = new CompositeImpl ("name");
		List<ValueElement> nameCopositeElements = new ArrayList <ValueElement> ();
		nameCopositeElements.add (new FieldImpl ("firstname"));
		nameCopositeElements.add (new FieldImpl ("lastname"));
		nameComposite.setElements (nameCopositeElements);
		cusElements.add (nameComposite);
		
		cusElements.add (new FieldImpl ("state"));
		
		cus.setElements (cusElements);
		
		// ORD Segment
		SegmentImpl ord = new SegmentImpl ("ORD");
		ord.setMaxOccurs (-1);
		List<ValueElement> ordElements = new ArrayList <ValueElement> ();
		ordElements.add (new FieldImpl ("position"));
		ordElements.add (new FieldImpl ("quantity"));
		ordElements.add (new FieldImpl ("product-id"));
		ordElements.add (new FieldImpl ("title"));
		ordElements.add (new FieldImpl ("price"));
		ord.setElements (ordElements);
		
		List<Container> containers = new ArrayList <Container> ();
		root.setContainers (containers);
		containers.add (hdr);
		containers.add (cus);
		containers.add (ord);
		
		ModelImpl model = new ModelImpl ();
		model.setRoot (root);
		model.setAgency ("MyAgency");
		model.setRelease("V-1.0");
		model.setStandard ("XYZ");
		
		new DefaultReader (new DefaultTokenizer ()).setModel (model).read (
			new FileInputStream ("files/example.edi"), 
			new ReaderOutputHandler (new FileWriter ("files/example.xml"))
		);
	}

}
