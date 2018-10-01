package net.wenbaobao;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;

public class Music {
    static volatile boolean stop = false;

    //播放音频文件
    public void play() {
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(Music.class.getResource("/backMusic.wav"));
            AudioFormat aif = ais.getFormat();
            //System.out.println(aif);

            final SourceDataLine sdl;
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, aif);
            sdl = (SourceDataLine) AudioSystem.getLine(info);
            sdl.open(aif);
            sdl.start();

            FloatControl fc = (FloatControl)sdl.getControl(FloatControl.Type.MASTER_GAIN);
            //value可以用来设置音量，从0-2.0
            double value = 0.3;
            float dB = (float)(Math.log(value == 0.0 ? 0.0001 : value) / Math.log(10.0) * 20.0);
            fc.setValue(dB);

            int nByte = 0;
            //int writeByte = 0;
            final int SIZE	= 1024 * 64;
            byte[] buffer 	= new byte[SIZE];

            while (nByte != -1) {
                nByte = ais.read(buffer, 0, SIZE);

                if(nByte > 0) {
                    sdl.write(buffer, 0, nByte);
                }

            }

            //sdl.stop();
            play();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}