package marrit.marritleenstra_pset62;

import java.io.Serializable;

public class Community implements Serializable {

    // variables
    private int mSumDays;
    private double mSumAnimals;
    private double mSumCO2;
    private int mSumParticipantsToday;
    private int mSumParticipants;

    // constructor
    public Community(){
        mSumDays = 0;
        mSumAnimals = 0;
        mSumCO2 = 0;
        mSumParticipantsToday = 0;
        mSumParticipants = 0;
    }

    // constructor
    public Community(int SumDays, double SumAnimals, double SumCO2, int SumParticipants, int SumParticipantsToday){
        this.mSumDays = SumDays;
        this.mSumAnimals = SumAnimals;
        this.mSumCO2 = SumCO2;
        this.mSumParticipants = SumParticipants;
        this.mSumParticipantsToday = SumParticipantsToday;
    }

    // getters and setters
    public int getSumDays() {
        return mSumDays;
    }

    public void setSumDays(int sumDays) {
        mSumDays = sumDays;
    }

    public double getSumAnimals() {
        return mSumAnimals;
    }

    public void setSumAnimals(double sumAnimals) {
        mSumAnimals = sumAnimals;
    }

    public double getSumCO2() {
        return mSumCO2;
    }

    public void setSumCO2(double sumCO2) {
        mSumCO2 = sumCO2;
    }

    public int getSumParticipantsToday() {
        return mSumParticipantsToday;
    }

    public void setSumParticipantsToday(int sumParticipantsToday) {
        mSumParticipantsToday = sumParticipantsToday;
    }

    public int getSumParticipants() {
        return mSumParticipants;
    }

    public void setSumParticipants(int sumParticipants) {
        mSumParticipants = sumParticipants;
    }
}
