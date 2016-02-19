/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.8
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.pjsip.pjsua2;

public final class pjmedia_type {
  public final static pjmedia_type PJMEDIA_TYPE_NONE = new pjmedia_type("PJMEDIA_TYPE_NONE");
  public final static pjmedia_type PJMEDIA_TYPE_AUDIO = new pjmedia_type("PJMEDIA_TYPE_AUDIO");
  public final static pjmedia_type PJMEDIA_TYPE_VIDEO = new pjmedia_type("PJMEDIA_TYPE_VIDEO");
  public final static pjmedia_type PJMEDIA_TYPE_APPLICATION = new pjmedia_type("PJMEDIA_TYPE_APPLICATION");
  public final static pjmedia_type PJMEDIA_TYPE_UNKNOWN = new pjmedia_type("PJMEDIA_TYPE_UNKNOWN");

  public final int swigValue() {
    return swigValue;
  }

  public String toString() {
    return swigName;
  }

  public static pjmedia_type swigToEnum(int swigValue) {
    if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
      return swigValues[swigValue];
    for (int i = 0; i < swigValues.length; i++)
      if (swigValues[i].swigValue == swigValue)
        return swigValues[i];
    throw new IllegalArgumentException("No enum " + pjmedia_type.class + " with value " + swigValue);
  }

  private pjmedia_type(String swigName) {
    this.swigName = swigName;
    this.swigValue = swigNext++;
  }

  private pjmedia_type(String swigName, int swigValue) {
    this.swigName = swigName;
    this.swigValue = swigValue;
    swigNext = swigValue+1;
  }

  private pjmedia_type(String swigName, pjmedia_type swigEnum) {
    this.swigName = swigName;
    this.swigValue = swigEnum.swigValue;
    swigNext = this.swigValue+1;
  }

  private static pjmedia_type[] swigValues = { PJMEDIA_TYPE_NONE, PJMEDIA_TYPE_AUDIO, PJMEDIA_TYPE_VIDEO, PJMEDIA_TYPE_APPLICATION, PJMEDIA_TYPE_UNKNOWN };
  private static int swigNext = 0;
  private final int swigValue;
  private final String swigName;
}

