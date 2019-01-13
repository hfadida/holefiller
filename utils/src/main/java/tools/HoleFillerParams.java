package tools;

public class HoleFillerParams {
    private String imgFilename;
    private String maskFilename;
    private float epsilon;
    private int z;
    private int connectivity;

    public String getImgFilename() {
        return imgFilename;
    }

    public void setImgFilename(String imgFilename) {
        this.imgFilename = imgFilename;
    }

    public String getMaskFilename() {
        return maskFilename;
    }

    public void setMaskFilename(String maskFilename) {
        this.maskFilename = maskFilename;
    }

    public float getEpsilon() {
        return epsilon;
    }

    public void setEpsilon(float epsilon) {
        this.epsilon = epsilon;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public int getConnectivity() {
        return connectivity;
    }

    public void setConnectivity(int connectivity) {
        this.connectivity = connectivity;
    }
}
