package edu.pitt.dbmi.deep.phe.util;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.pitt.dbmi.nlp.noble.ontology.IClass;
import edu.pitt.dbmi.nlp.noble.ontology.IOntology;
import edu.pitt.dbmi.nlp.noble.ontology.IOntologyException;
import edu.pitt.dbmi.nlp.noble.ontology.owl.OOntology;
import edu.pitt.dbmi.nlp.noble.terminology.Concept;

public class OntologyUtils {
	
	/**
	 * save dictionary as BSV file
	 * @param root
	 * @param output
	 * @throws IOException 
	 */
	public static void saveDictionary(IClass root, File output) throws IOException{
		// write out BSV file
		BufferedWriter w = new BufferedWriter(new FileWriter(output));
		w.write(convertCls(root));
		for(IClass c: root.getSubClasses()){
			w.write(convertCls(c));
		}
		w.close();
	}
	
	
	/**
	 * convert Class to BSV entry
	 * @param root
	 * @return
	 */
	private static String convertCls(IClass cls) {
		Concept c = cls.getConcept();
		// find UMLS CUIS
		String cui = null;
		for(Object cc : c.getCodes().values()){
			Matcher m = Pattern.compile("(CL?\\d{6,7})( .+)?").matcher(cc.toString());
			if(m.matches()){
				cui = m.group(1);
				break;
			}
		}
		if(cui != null){
			String tui = "";
			if(c.getSemanticTypes().length > 0)
				tui = c.getSemanticTypes()[0].getCode();
			StringBuffer b = new StringBuffer();
			for(String s: c.getSynonyms()){
				b.append(cui+"|"+tui+"|"+s+"\n");
			}
			return b.toString();
		}else{
			System.out.println("No CUI in cls "+cls.getName());
		}
		return "";
	}



	public static void main(String [] args) throws Exception{
		File f = new File("/home/tseytlin/Work/DeepPhe_ontologies/breastCancer.owl");
		File of = new File("/home/tseytlin/Work/DeepPhe_ontologies/breastCancer.bsv");
		IOntology ont = OOntology.loadOntology(f);
		saveDictionary( ont.getClass("Element"),of);
		System.out.println("done");
	}
}