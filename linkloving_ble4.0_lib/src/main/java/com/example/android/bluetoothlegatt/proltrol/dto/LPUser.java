package com.example.android.bluetoothlegatt.proltrol.dto;

public class LPUser {
	public int userId;   // 用户ID，只能使用低28bit
	public int sex;      // 性别，0:女 1:男
	public int age;      // 年龄 0 ~ 127
	public int height;   // 身高，cm
	public int weight;   // 体重，kg
	public int recoderStatus;  //激活状态

	public LPUser(){}
	public LPUser(int userId, int sex, int age, int height, int weight) {
		this.userId = userId;
		this.sex = sex;
		this.age = age;
		this.height = height;
		this.weight = weight;
	}
	@Override
	public String toString() {
		return "User [userId=" + userId + ", sex=" + sex + ", age=" + age + ", height=" + height + ", weight=" + weight + ", recoderStatus="+recoderStatus+"]";
	}
	
	public int getRecoderStatus() {
		return recoderStatus;
	}
	public void setRecoderStatus(int recoderStatus) {
		this.recoderStatus = recoderStatus;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getSex() {
		return sex;
	}
	public void setSex(int sex) {
		this.sex = sex;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
}
