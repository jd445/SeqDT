package SeqDTCode;

/**
 * MeasureIndex
 */
public class MeasureIndex {

    private float accuracy;
    private float specificity;
    private float sensitivity;
    private float MCC;
    private float TP;
    private float TN;
    private float FP;
    private float FN;

    public MeasureIndex(float TP, float TN, float FP, float FN) {
        this.FN = FN;
        this.FP = FP;
        this.TN = TN;
        this.TP = TP;
    }

    public float getAccuracy() {
        accuracy = (TP + TN) / (TP + TN + FP + FN);
        return accuracy;
    }

    public float getSpecificity() {
        specificity = TN / (TN + FP);
        return specificity;
    }

    public float getSensitivity() {
        sensitivity = TP / (TP + FN);
        return sensitivity;

    }

    public float getMCC() {
        // System.out.println("Measure Index " + TP + " " + TN + " " + FP + " " + FN);
        MCC = (float) ((TP * TN - FP * FN) / (Math.sqrt(((TP + FP) * (TP + FN) * (TN + FP) * (TN + FN)))));
        return MCC;
    }

    public static void main(String[] args) {
        MeasureIndex a = new MeasureIndex(3200, 3205, 1356, 1239);
        System.out.println(a.getMCC());
    }
}
