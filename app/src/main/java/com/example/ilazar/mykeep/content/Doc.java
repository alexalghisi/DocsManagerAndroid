package com.example.ilazar.mykeep.content;

public class Doc {

  public enum Status {
    active,
    archived;

  }
  private String mId;
  private String mUserId;
  private String mText;
  private String mTitle;
  private String mDate;
  private Status mStatus = Status.active;
  private long mUpdated;
  private int mNersion;

  public Doc() {
  }

  public String getId() {
    return mId;
  }

  public void setId(String id) {
    mId = id;
  }

  public String getUserId() {
    return mUserId;
  }

  public void setUserId(String userId) {
    mUserId = userId;
  }

  public String getText() {
    return mText;
  }

  public String getTitle() { return mTitle; }

  public String getDate() { return mDate; }

  public void setText(String text) {
    mText = text;
  }

  public void setTitle(String title) { mTitle = title; }

  public void setDate(String date) { mDate = date; }

  public Status getStatus() {
    return mStatus;
  }

  public void setStatus(Status status) {
    mStatus = status;
  }

  public long getUpdated() {
    return mUpdated;
  }

  public void setUpdated(long updated) {
    mUpdated = updated;
  }

  public int getVersion() {
    return mNersion;
  }

  public void setVersion(int version) {
    mNersion = version;
  }

  @Override
  public String toString() {
    return "Doc{" +
        "mId='" + mId + '\'' +
        ", mUserId='" + mUserId + '\'' +
        ", mText='" + mText + '\'' +
        ", mStatus=" + mStatus +
        ", mUpdated=" + mUpdated +
        ", mNersion=" + mNersion +
        '}';
  }
}
