package io.security.corespringsecurity.controller.user;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MessageController {

	@PostMapping(value="/api/messages")
	@ResponseBody
	public String apiMessages() throws Exception {
		return "messages ok";
	}
}
