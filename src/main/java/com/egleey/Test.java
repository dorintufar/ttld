package com.egleey;

import javax.sound.sampled.*;
import java.io.*;

public class Test {
    public static void main(String[] argc) throws LineUnavailableException {

//        byte[] buf = new byte[ 1 ];;
//        AudioFormat af = new AudioFormat( (float )44100, 8, 1, true, false );
//        SourceDataLine sdl = AudioSystem.getSourceDataLine( af );
//        sdl.open();
//        sdl.start();
//        for( int i = 0; i < 1000 * (float )44100 / 1000; i++ ) {
//            double angle = i / ( (float )44100 / 440 ) * 2.0 * Math.PI;
//            buf[ 0 ] = (byte )( Math.sin( angle ) * 100 );
//            sdl.write( buf, 0, 1 );
//        }
//        sdl.drain();
//        sdl.stop();
//
//        System.exit(1);

        Test test = new Test();
        test.playSound(new File("src/main/webapp/resources/audio/Tool - Jambi.mp3"));

        System.exit(1);

        AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);
        TargetDataLine microphone;
        AudioInputStream audioInputStream;
        SourceDataLine sourceDataLine;

        try {
            microphone = AudioSystem.getTargetDataLine(format);

            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int numBytesRead;
            int CHUNK_SIZE = 1024;
            byte[] data = new byte[microphone.getBufferSize() / 5];
            microphone.start();

            int bytesRead = 0;

            try {
                while (bytesRead < 100000) { // Just so I can test if recording
                    // my mic works...
                    numBytesRead = microphone.read(data, 0, CHUNK_SIZE);
                    bytesRead = bytesRead + numBytesRead;
                    System.out.println(bytesRead);
                    out.write(data, 0, numBytesRead);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            byte audioData[] = out.toByteArray();
            // Get an input stream on the byte array
            // containing the data
            InputStream byteArrayInputStream = new ByteArrayInputStream(
                    audioData);
            audioInputStream = new AudioInputStream(byteArrayInputStream,format, audioData.length / format.getFrameSize());
            DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
            sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            sourceDataLine.open(format);
            sourceDataLine.start();
            int cnt = 0;
            byte tempBuffer[] = new byte[10000];
            try {
                while ((cnt = audioInputStream.read(tempBuffer, 0,tempBuffer.length)) != -1) {
                    if (cnt > 0) {
                        // Write data to the internal buffer of
                        // the data line where it will be
                        // delivered to the speaker.
                        sourceDataLine.write(tempBuffer, 0, cnt);
                    }// end if
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Block and wait for internal buffer of the
            // data line to empty.
            sourceDataLine.drain();
            sourceDataLine.close();
            microphone.close();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    private final int BUFFER_SIZE = 128000;
    private AudioInputStream audioStream;
    private AudioFormat audioFormat;
    private SourceDataLine sourceLine;

    public void playSound(File soundFile) {
        try {
            audioStream = AudioSystem.getAudioInputStream(soundFile);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        audioFormat = audioStream.getFormat();
        DataLine.Info infoIn = new DataLine.Info(SourceDataLine.class,
                audioFormat);
        try {
            Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
            Mixer mixer = null;
            for (int i = 0; i < mixerInfos.length; i++) {
                System.out.println(mixerInfos[i].getName());
                if (mixerInfos[i].getName().equals(
                        "CABLE Input (VB-Audio Virtual Cable)")) {
                    mixer = AudioSystem.getMixer(mixerInfos[i]);
                    break;
                }
            }
            sourceLine = (SourceDataLine) mixer.getLine(infoIn);
            sourceLine.open(audioFormat);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
            System.exit(1);
        }
        sourceLine.start();
        int nBytesRead = 0;
        byte[] abData = new byte[BUFFER_SIZE];
        while (nBytesRead != -1) {
            try {
                nBytesRead = audioStream.read(abData, 0, abData.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (nBytesRead >= 0) {
                @SuppressWarnings("unused")
                int nBytesWritten = sourceLine.write(abData, 0, nBytesRead);
            }
        }
        sourceLine.drain();
        sourceLine.close();
    }
}
