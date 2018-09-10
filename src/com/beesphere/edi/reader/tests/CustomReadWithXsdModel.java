package com.beesphere.edi.reader.tests;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import com.beesphere.edi.OutputHandler;
import com.beesphere.edi.model.Element;
import com.beesphere.edi.model.impl.xsd.XsdModel;
import com.beesphere.edi.reader.ReaderException;
import com.beesphere.edi.reader.impls.DefaultReader;
import com.beesphere.xsd.XsdParser;
import com.beesphere.xsd.XsdParserException;

public class CustomReadWithXsdModel {

	public static void main(String[] args) throws ReaderException, XsdParserException, IOException {
		XsdParser parser = new XsdParser ();
		parser.parse (new FileInputStream ("files/837.xsd"));
		
		new DefaultReader ().setModel (new XsdModel (parser.getSchema ())).read (
			new FileInputStream ("files/837.edi"), 
			new OutputHandler () {

				private static final long serialVersionUID = 2949480722741551187L;

				private Writer writer = new FileWriter ("files/out.txt");
				@Override
				public void onData(Element element, Kind kind, String value)
						throws IOException {
					if (kind.equals (OutputHandler.Kind.FIELD) && value != null) {
						onStart (element, kind);   
						writer.append (value);   
						writer.append("\n");
						onEnd (element, kind);
						writer.append("\n");
					}
				}

				@Override
				public void onEnd(Element element, Kind kind)
						throws IOException {
					if (!kind.equals (Kind.DOCUMENT)){
						writer.append("End: ").append(element.getName() + "\n");
					}
					writer.flush();
				}

				@Override
				public void onStart(Element element, Kind kind)
						throws IOException {
					if (!kind.equals (Kind.DOCUMENT)){
						writer.append("Start : " + element.getName() + ":\n");
					}
				}
				
			}
		);
		
	}
	
}
