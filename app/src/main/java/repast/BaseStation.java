/*
 * Created on Thu Oct 27 2022
 * @author Rui Zuo
 */

package repast;

/*
 * Represents a plain base station. Including configuration parameters and real-
 * time number of attached UEs
 */
public class BaseStation {
  private int id;
  private double lat; // Latitude
  private double lng; // Longitude
  private double txPower; // Transmission Power
  private int bandwidth;
  private int subBandwidth; // Bandwidth used in Hard Frequency reuse in NS3
  private int subBandOffset; // Bandwidth offset used in Hard Frequency reuse in
                             // NS3
  private int numberOfAttachedUe = 0;

  public int getId() {
    return id;
  }

  public BaseStation setId(int id) {
    this.id = id;
    return this;
  }

  public double getLat() {
    return lat;
  }

  public BaseStation setLat(double lat) {
    this.lat = lat;
    return this;
  }

  public double getLng() {
    return lng;
  }

  public BaseStation setLng(double lng) {
    this.lng = lng;
    return this;
  }

  public double getTxPower() {
    return txPower;
  }

  public BaseStation setTxPower(double txPower) {
    this.txPower = txPower;
    return this;
  }

  public int getBandwidth() {
    return bandwidth;
  }

  public BaseStation setBandwidth(int bandwidth) {
    this.bandwidth = bandwidth;
    return this;
  }

  public int getSubBandwidth() {
    return subBandwidth;
  }

  public BaseStation setSubBandwidth(int subBandwidth) {
    this.subBandwidth = subBandwidth;
    return this;
  }

  public int getSubBandOffset() {
    return subBandOffset;
  }

  public BaseStation setSubBandOffset(int subBandOffset) {
    this.subBandOffset = subBandOffset;
    return this;
  }

  public int getNumberOfAttachedUe() {
    return numberOfAttachedUe;
  }

  public BaseStation setNumberOfAttachedUe(int numberOfAttachedUe) {
    this.numberOfAttachedUe = numberOfAttachedUe;
    return this;
  }

  private BaseStation(Builder builder) {
    this.id = builder.id;
    this.lat = builder.lat;
    this.lng = builder.lng;
    this.txPower = builder.txPower;
    this.bandwidth = builder.bandwidth;
    this.subBandwidth = builder.subBandwidth;
    this.subBandwidth = builder.subBandwidth;
    this.subBandOffset = builder.subBandOffset;
    this.numberOfAttachedUe = builder.numberOfAttachedUe;
  }

  public static class Builder {
    private int id;
    private double lat = 0.0;
    private double lng = 0.0;
    private double txPower = 15.0 ;
    private int bandwidth = 15;
    private int subBandwidth = 6;
    private int subBandOffset = 1;
    private int numberOfAttachedUe = 0;

    public Builder(int id) {
      this.id = id;
    }

    public BaseStation build() {
      return new BaseStation(this);
    }

    public Builder numberOfAttachedUe(int numberOfAttachedUe) {
      this.numberOfAttachedUe = numberOfAttachedUe;
      return this;
    }

    public Builder subBandOffset(int subBandOffset) {
      this.subBandOffset = subBandOffset;
      return this;
    }

    public Builder subBandwidth(int subBandwidth) {
      this.subBandwidth = subBandwidth;
      return this;
    }

    public Builder bandwidth(int bandwidth) {
      this.bandwidth = bandwidth;
      return this;
    }    

    public Builder txPower(double txPower) {
      this.txPower = txPower;
      return this;
    }
    
    public Builder lat(double lat) {
      this.lat = lat;
      return this;
    }

    public Builder lng(double lng) {
      this.lng = lng;
      return this;
    }
  }

  @Override
  public String toString() {
    return "BaseStation [id=" + id + ", lat=" + lat + ", lng=" + lng + ", txPower=" + txPower + ", bandwidth="
        + bandwidth + ", subBandwidth=" + subBandwidth + ", subBandOffset=" + subBandOffset + ", numberOfAttachedUe="
        + numberOfAttachedUe + "]";
  }
}
