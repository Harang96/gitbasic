package com.webljy.service;

import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.webljy.controller.MemberFactory;

public interface MemberService {
	MemberFactory executeService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
}
