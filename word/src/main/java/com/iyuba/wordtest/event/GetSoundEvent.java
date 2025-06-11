package com.iyuba.wordtest.event;

public class GetSoundEvent {


    public String sentenceUrl;
    public int voaId ;


    public int idIndex;
    public int paraid;

    public GetSoundEvent(String sentenceUrl,int voaId, int paraid, int idIndex) {
        this.sentenceUrl = sentenceUrl;
        this.voaId = voaId;
        this.idIndex = idIndex;
        this.paraid = paraid;
    }

    public GetSoundEvent(int voaId, int paraid, int idIndex) {
        this.voaId = voaId;
        this.idIndex = idIndex;
        this.paraid = paraid;
    }
}
