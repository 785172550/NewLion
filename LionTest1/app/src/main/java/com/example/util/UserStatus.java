package com.example.util;

import com.example.model.User;

import java.util.List;

/**
 * Created by Deep on 16/3/15.
 */
public class UserStatus {
	public static Boolean hasUserLogined() {
		List<User> users = User.findWithQuery(User.class, "Select * from User");
		if (users.size() == 0) {
			return false;
		} else  {
			return true;
		}
	}

	public static Boolean hasUserCompletedInfo() {
		List<User> users = User.findWithQuery(User.class, "Select * from User");
		// 判断用户的资料是否填写完整
		if (users.get(0).getName().isEmpty() || users.get(0).getName() == null) {
			return false;
		} else  {
			// 用户资料已经填写完整
			return true;
		}
	}

	public static User getLoginedUser() {
		List<User> users = User.findWithQuery(User.class, "Select * from User");
		return users.get(0);
	}

		public static void deleteLoginedUser() {

			List<User> users = User.findWithQuery(User.class, "Select * from User");
		if (!users.get(0).getName().isEmpty() || users.get(0).getName() != null)
			users.get(0).delete();
    }
}
