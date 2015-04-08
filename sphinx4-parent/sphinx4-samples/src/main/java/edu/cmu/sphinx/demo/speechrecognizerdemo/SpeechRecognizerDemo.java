/*
 * Copyright 1999-2013 Carnegie Mellon University.
 * Portions Copyright 2004 Sun Microsystems, Inc.
 * Portions Copyright 2004 Mitsubishi Electric Research Laboratories.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 */

package edu.cmu.sphinx.demo.speechrecognizerdemo;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.List;



//import javax.sound.sampled.AudioFileFormat;
//import javax.sound.sampled.AudioFormat;
//import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;
import edu.cmu.sphinx.decoder.adaptation.Stats;
import edu.cmu.sphinx.decoder.adaptation.Transform;
import edu.cmu.sphinx.demo.allphone.AllphoneDemo;
import edu.cmu.sphinx.demo.transcriber.TranscriberDemo;
import edu.cmu.sphinx.result.WordResult;

/**
 * A simple example that shows how to transcribe a continuous audio file that
 * has multiple utterances in it.
 */
public class SpeechRecognizerDemo {

    public static void main(String[] args) throws Exception {
        //System.out.println("Loading models...");

        Configuration configuration = new Configuration();

        // Load models
        configuration
                .setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/cmusphinx-en-us-5.2");
        configuration
                .setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
        configuration
                .setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.dmp");

        StreamSpeechRecognizer recognizer = new StreamSpeechRecognizer(
                configuration);        

        SpeechResult result;
                      
        // open file for the first time to begin recognition
        InputStream stream = new BufferedInputStream(TranscriberDemo.class
        		.getResourceAsStream("/edu/cmu/sphinx/demo/aligner/10001-90210-01803.wav"));
        //10001-90210-01803
        //nsa_NoNoise_converted
        //File file = new File("/edu/cmu/sphinx/demo/aligner/10001-90210-01803.wav");
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(stream);
        int channels = audioInputStream.getFormat().getChannels();
        float sampleRate = audioInputStream.getFormat().getSampleRate();
        int bps = audioInputStream.getFormat().getSampleSizeInBits();
                
        //audioInputStream.close();
        long bytesToSkip = 0;
        
        
        stream.skip(bytesToSkip);
    	recognizer.startRecognition(stream);
                
        while ((result = recognizer.getResult()) != null) {  
        	
        	// print hypothesis
            System.out.format("Hypothesis: %s\n", result.getHypothesis());
            
            // print list of words recognized
            System.out.println("List of recognized words and their times:");
            List<WordResult> wordList = result.getWords();
            for (WordResult word : wordList) {
                System.out.println(word);
            }

            // print alternative hypotheses
            System.out.println("Best 3 hypothesis:");
            for (String s : result.getNbest(3))
                System.out.println(s);

            // print out lattice
            System.out.println("Lattice contains "
                    + result.getLattice().getNodes().size() + " nodes");
                                 
            
            // get the timestamp of the end of the utterance
            long utteranceEndTime = wordList.get(wordList.size()-1).getTimeFrame().getEnd();   
            double endTimeSeconds = utteranceEndTime/1000.0;
            bytesToSkip += Math.round((endTimeSeconds) * sampleRate * channels * bps / 8.0);
            // stop recognition to release memory
            recognizer.stopRecognition();
            stream.close();
            // re-initialize the recognition process
            stream = new BufferedInputStream(TranscriberDemo.class
                    .getResourceAsStream("/edu/cmu/sphinx/demo/aligner/10001-90210-01803.wav"));
            
            // skip ahead to end of last utterance
            stream.skip(bytesToSkip);
        	recognizer.startRecognition(stream);
        }
      
        recognizer.stopRecognition();
        stream.close();
        // Live adaptation to speaker with speaker profiles

        stream = new BufferedInputStream(TranscriberDemo.class
                .getResourceAsStream("/edu/cmu/sphinx/demo/aligner/10001-90210-01803.wav"));
        stream.skip(44);

        // Stats class is used to collect speaker-specific data
        Stats stats = recognizer.createStats(1);
        recognizer.startRecognition(stream);
        while ((result = recognizer.getResult()) != null) {
            stats.collect(result);
        }
        recognizer.stopRecognition();

        // Transform represents the speech profile
        Transform transform = stats.createTransform();
        recognizer.setTransform(transform);

        // Decode again with updated transform
        stream = new BufferedInputStream(TranscriberDemo.class
                .getResourceAsStream("/edu/cmu/sphinx/demo/aligner/10001-90210-01803.wav"));
        stream.skip(44);
        recognizer.startRecognition(stream);
        while ((result = recognizer.getResult()) != null) {
            System.out.format("Hypothesis: %s\n", result.getHypothesis());
        }
        recognizer.stopRecognition();

    }
}
