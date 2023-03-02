package prediction.input;

public class InputFromBaseStation {
  public int id;
  public double distance;
  public double txPower; // Transmission Power
  public int bandwidth;
  public int subBandwidth; // Bandwidth used in Hard Frequency reuse in NS3
  public int subBandOffset; // Bandwidth offset used in Hard Frequency reuse in
                             // NS3
  public int numberOfAttachedUe; 

  public InputFromBaseStation(int id, double distance, double txPower,
    int bandwidth, int subBandwidth, int subBandOffset, int numberOfAttachedUe) {
    this.id = id;
    this.distance = distance;
    this.txPower = txPower;
    this.bandwidth = bandwidth;
    this.subBandwidth = subBandwidth;
    this.subBandOffset = subBandOffset;
    this.numberOfAttachedUe = numberOfAttachedUe;
  }
  
  public float[] toArray() {
    float[] arr = new float[5];
    arr[0] = (float) distance;
    arr[1] = (float) txPower;
    arr[2] = (float) bandwidth;
    arr[3] = (float) subBandwidth;
    arr[4] = (float) subBandOffset;
    return arr;
  }

  @Override
  public String toString() {
    return "InputFromBaseStation [id=" + id + ", distance=" + distance + ", txPower=" + txPower + ", bandwidth="
        + bandwidth + ", subBandwidth=" + subBandwidth + ", subBandOffset=" + subBandOffset + ", numberOfAttachedUe="
        + numberOfAttachedUe + "]";
  }
}
