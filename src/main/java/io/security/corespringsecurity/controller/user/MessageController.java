package io.security.corespringsecurity.controller.user;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class MessageController {

	@GetMapping(value="/api/messages")
	@ResponseBody
	public String apiMessages() throws Exception {
		return "messages ok";
	}
}
