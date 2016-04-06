package uk.co.mayfieldis.jorvik.core;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.hl7.fhir.instance.formats.IParser.OutputStyle;
import org.hl7.fhir.instance.formats.JsonParser;
import org.hl7.fhir.instance.formats.ParserType;
import org.hl7.fhir.instance.formats.XmlParser;
import org.hl7.fhir.instance.model.Bundle;
import org.hl7.fhir.instance.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceSerialiser {
	private static Logger logger = LoggerFactory.getLogger(ResourceSerialiser.class);
	
	public static String serialise(Resource resource, ParserType type) {
		try {
			if (type == ParserType.XML) {
				XmlParser composer = new XmlParser();
				OutputStream out = new ByteArrayOutputStream();
				composer.setOutputStyle(OutputStyle.PRETTY);
				composer.compose(out, resource, true);
				return out.toString();
			} else {
				JsonParser composer = new JsonParser();
				OutputStream out = new ByteArrayOutputStream();
				composer.setOutputStyle(OutputStyle.PRETTY);
				
				composer.compose(out, resource);
				return out.toString();
			}
		} catch (Exception e) {
			logger.error("Unable to serialise FHIR resource " + resource.toString(), e);
		}
		return null;
	}
	
	public static String serialise(Bundle feed, ParserType type) {
		try {
			if (type == ParserType.XML) {
				XmlParser composer = new XmlParser();
				OutputStream out = new ByteArrayOutputStream();
				composer.compose(out, feed, true);
				return out.toString();
			} else {
				JsonParser composer = new JsonParser();
				OutputStream out = new ByteArrayOutputStream();
				composer.setOutputStyle(OutputStyle.PRETTY);
				composer.compose(out, feed);
				return out.toString();
			}
		} catch (Exception e) {
			logger.error("Unable to serialise FHIR Bundle ", e);
		}
		return null;
	}
}
