<%@page import="java.awt.Color,nl.captcha.*,nl.captcha.servlet.*,nl.captcha.text.renderer.*,nl.captcha.gimpy.*,nl.captcha.noise.*,nl.captcha.backgrounds.*"%>
<%
	// The captcha answer is stored in a session attribute given by value of id-parameter.
	if (request.getParameter("id") != null) {
		Captcha captcha = new Captcha.Builder(170, 75)
				.addText(new ColoredEdgesWordRenderer())
				.addBackground(new GradiatedBackgroundProducer(Color.WHITE, Color.LIGHT_GRAY))
				.addNoise(new StraightLineNoiseProducer(Color.LIGHT_GRAY, 2))
				.addNoise(new CurvedLineNoiseProducer(Color.DARK_GRAY, 1f))
				.addNoise(new CurvedLineNoiseProducer(Color.WHITE, 2f))
				.gimp(new RippleGimpyRenderer())
				.gimp(new DropShadowGimpyRenderer()).build();
		session.setAttribute(request.getParameter("id"), captcha.getAnswer());
		CaptchaServletUtil.writeImage(response, captcha.getImage());
	}
%>