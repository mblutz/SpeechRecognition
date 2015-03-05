/*
 * Copyright 2013 Carnegie Mellon University.
 * Portions Copyright 2004 Sun Microsystems, Inc.
 * Portions Copyright 2004 Mitsubishi Electric Research Laboratories.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 */

package edu.cmu.sphinx.demo.dialog;

import java.io.InputStream;
import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;
import edu.cmu.sphinx.demo.allphone.AllphoneDemo;


public class DialogDemo {

    private static final String ACOUSTIC_MODEL = "resource:/edu/cmu/sphinx/models/en-us/cmusphinx-5prealpha-en-us-ptm-2.0";
    private static final String DICTIONARY_PATH = "resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict";
    private static final String GRAMMAR_PATH = "resource:/edu/cmu/sphinx/demo/dialog/";
    private static final String LANGUAGE_MODEL = "resource:/edu/cmu/sphinx/demo/dialog/en-us.lm.dmp";


    private static void recognizeDigits(StreamSpeechRecognizer recognizer) throws Exception {
        System.out.println("Attempting to recognize words from audio file");
        
        InputStream stream = AllphoneDemo.class
                .getResourceAsStream("/edu/cmu/sphinx/demo/aligner/nsa_NoNoise_converted.wav");
        stream.skip(44);
        
        recognizer.startRecognition(stream);
    	SpeechResult result = recognizer.getResult();
        while(result != null) {        	         
        	String utterance = result.getHypothesis();
            System.out.println(utterance);        	
        	result = recognizer.getResult();                		
        } 
        stream.close();
    }

    public static void main(String[] args) throws Exception {
        Configuration configuration = new Configuration();
        configuration.setAcousticModelPath(ACOUSTIC_MODEL);
        configuration.setDictionaryPath(DICTIONARY_PATH);
        configuration.setGrammarPath(GRAMMAR_PATH);
        configuration.setUseGrammar(false);

        //configuration.setGrammarName();
        configuration.setLanguageModelPath(LANGUAGE_MODEL);
        StreamSpeechRecognizer lmRecognizer =
            new StreamSpeechRecognizer(configuration);

        
        recognizeDigits(lmRecognizer);        
    }
}
