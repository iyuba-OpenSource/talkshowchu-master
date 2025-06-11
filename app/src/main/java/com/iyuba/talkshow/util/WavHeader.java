package com.iyuba.talkshow.util;

import java.io.DataOutputStream;
import java.io.FileOutputStream;

public class WavHeader {

    interface Style {
        int WAV_FILE_HEADER_SIZE = 44;
        int WAV_CHUNKSIZE_EXCLUDE_DATA = 36;
        int WAV_CHUNKSIZE_OFFSET = 4;
        int WAV_SUB_CHUNKSIZE1_OFFSET = 16;
        int WAV_SUB_CHUNKSIZE2_OFFSET = 40;   
    }

    private static final String CHUNK_ID = "RIFF";
    private static final int CHUNK_SIZE = 0;
    private static final String FORMAT = "WAVE";

    private static final String SUB_CHUNK1_ID = "fmt ";
    private static final int SUB_CHUNK1_SIZE = 16;
    private static final short AUDIO_FORMAT = 1;

    private final static String SUB_CHUNK2_ID = "data";
    private final static int SUB_CHUNK2_SIZE = 0;

    private short mBitsPerSample = 8;
    private short mNumChannel = 1;
    private int mSampleRate = 8000;
    private int mByteRate = 0;
    private short mBlockAlign = 0;

    private WavHeader() {

    }

    private WavHeader(int sampleRateInHz, int mBitsPerSample, int channels) {
        this.mSampleRate = sampleRateInHz;
        this.mBitsPerSample = (short) mBitsPerSample;
        this.mNumChannel = (short) channels;
        this.mByteRate = mSampleRate * mNumChannel * this.mBitsPerSample / 8;
        this.mBlockAlign = (short) (mNumChannel * this.mBitsPerSample / 8);
    }

    public boolean writeHeader(String filePath) {
        try {
            DataOutputStream dos = new DataOutputStream(new FileOutputStream(filePath));

            dos.writeBytes(CHUNK_ID);
            dos.write(TypeConvertUtil.intToByteArray(CHUNK_SIZE), 0, 4);
            dos.writeBytes(FORMAT);
            dos.writeBytes(SUB_CHUNK1_ID);
            dos.write(TypeConvertUtil.intToByteArray(SUB_CHUNK1_SIZE), 0, 4);
            dos.write(TypeConvertUtil.shortToByteArray(AUDIO_FORMAT), 0, 2);
            dos.write(TypeConvertUtil.shortToByteArray(mNumChannel), 0, 2);
            dos.write(TypeConvertUtil.intToByteArray(mSampleRate), 0, 4);
            dos.write(TypeConvertUtil.intToByteArray(mByteRate), 0, 4);
            dos.write(TypeConvertUtil.shortToByteArray(mBlockAlign), 0, 2);
            dos.write(TypeConvertUtil.shortToByteArray(mBitsPerSample), 0, 2);
            dos.writeBytes(SUB_CHUNK2_ID);
            dos.write(TypeConvertUtil.intToByteArray(SUB_CHUNK2_SIZE), 0, 4);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    static class Builder {
        private int sampleRateInHz;
        private int bitsPerSample;
        private int channels;

        public Builder setSampleRateInHz(int sampleRateInHz) {
            this.sampleRateInHz = sampleRateInHz;
            return this;
        }

        public Builder setBitsPerSample(int bitsPerSample) {
            this.bitsPerSample = bitsPerSample;
            return this;
        }

        public Builder setChannels(int channels) {
            this.channels = channels;
            return this;
        }

        public WavHeader builder() {
            return new WavHeader(sampleRateInHz, bitsPerSample, channels);
        }

    }
}

