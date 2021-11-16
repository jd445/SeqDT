package SeqDTCode;

public class AverageFoldRes {
    public float AveAccuracy;
    public float AveMCC;
    public float AveSensitivity;
    public float AveSpecificity;

    AverageFoldRes(float AveAccuracy, float AveMCC, float AveSensitivity, float AveSpecificity) {
        this.AveAccuracy = AveAccuracy;
        this.AveMCC = AveAccuracy;
        this.AveSensitivity = AveSensitivity;
        this.AveSpecificity = AveSpecificity;
    }

}
