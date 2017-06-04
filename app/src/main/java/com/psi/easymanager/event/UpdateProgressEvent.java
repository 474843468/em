package com.psi.easymanager.event;

/**
 * Created by dorado on 2016/6/21.
 */
public class UpdateProgressEvent {
  private int progress;

  public int getProgress() {
    return progress;
  }

  public UpdateProgressEvent setProgress(int progress) {
    this.progress = progress;
    return this;
  }
}
