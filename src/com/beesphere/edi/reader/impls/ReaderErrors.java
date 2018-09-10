package com.beesphere.edi.reader.impls;

public interface ReaderErrors {
	public static final String ELEMENT_TOO_LONG = "Too many characters in an element (delimiter problem?)";

	public static final String EXPECTED_SIMPLE_TOKEN = "Expected a simple token";

	public static final String INTERNAL_ERROR_MULTIPLE_PREVIEWS = "Internal error: interchange previewed more than once";

	public static final String INTERNAL_ERROR_MULTIPLE_EOFS = "End-of-file hit multiple times (internal error)";

	public static final String DIGITS_ONLY = "Element must contain only digits";

	public static final String INVALID_COMPOSITE = "Invalid composite element";

	public static final String UNEXPECTED_EOF = "Unexpected end of data";

	public static final String INVALID_BEGINNING_OF_SEGMENT = "Invalid beginning of segment";

	public static final String UNEXPECTED_SEGMENT_IN_CONTEXT = "Unexpected segment type in this context";

	public static final String X12_MISSING_ISA = "ANSI X.12 interchange must begin with ISA";

	public static final String TOO_MANY_ISA_FIELDS = "Too many fields for an ISA (Segment terminator problem?)";

	public static final String ISA_FIELD_WIDTH = "Incorrect length of fixed-length ISA field";

	public static final String INCOMPLETE_X12 = "Incomplete ANSI X.12 interchange";

	public static final String CONTROL_NUMBER_IEA = "Control number error in IEA segment";

	public static final String CONTROL_NUMBER_GE = "Control number error in GE segment";

	public static final String CONTROL_NUMBER_SE = "Control number error in SE segment";

	public static final String COUNT_IEA = "Functional group count error in IEA segment";

	public static final String COUNT_GE = "Transaction count error in GE segment";

	public static final String COUNT_SE = "Segment count error in SE segment";

	public static final String INVALID_UNA = "Improperly formed UNA segment";

	public static final String CONTROL_NUMBER_UNZ = "Control number error in UNZ segment";

	public static final String CONTROL_NUMBER_UNT = "Control number error in UNT segment";

	public static final String COUNT_UNZ = "Functional group count error in UNZ segment";

	public static final String COUNT_UNT = "Segment count error in UNT segment";

	public static final String FIRST_SEGMENT_MUST_BE_UNA_OR_UNB = "First segment must be UNA or UNB";

	public static final String NO_HL7_PARSER = "Data begins with MSH indicating HL7 data, but no HL7 parser is available";

	public static final String INCOMPLETE_HL7_MESSAGE = "Incomplete HL7 message";

	public static final String INCOMPLETE_ACH_MESSAGE = "Incomplete ACH file";

	public static final String NO_STANDARD_BEGINS_WITH = "No supported EDI standard interchange begins with ";

	public static final String MANDATORY_ELEMENT_MISSING = "Mandatory element missing";

	public static final String MISSING_UNP = "UNP segment not properly positioned after UNO segment and data object sequence";

	public static final String MISMATCHED_UNP_LENGTH = "UNP segment contains length field that does not match length field in UNO";

	public static final String MISMATCHED_PACKAGE_REF = "UNP segment contains package reference that does not match corresponding reference in UNO";

	public static final String MISSING_UNO_LENGTH = "UNO segment missing mandatory length field";

}
