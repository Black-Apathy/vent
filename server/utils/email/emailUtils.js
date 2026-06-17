const nodemailer = require("nodemailer");
const { getRegistrationOTPTemplate } = require("./emailTemplates");

const transporter = nodemailer.createTransport({
  host: process.env.SMTP_HOST || "smtp.gmail.com",
  port: process.env.SMTP_PORT || 587,
  secure: false,
  auth: {
    user: process.env.SMTP_USER,
    pass: process.env.SMTP_PASS,
  },
});

exports.sendOTPEmail = async (userEmail, otpCode) => {
  try {
    const mailOptions = {
      from: `"Vent Security" <${process.env.SMTP_USER}>`,
      to: userEmail,
      subject: "Your Vent Verification Code",
      text: `Your Vent registration code is: ${otpCode}. It expires in 5 minutes.`,
      html: getRegistrationOTPTemplate(otpCode), // Injecting the template here
    };

    const info = await transporter.sendMail(mailOptions);
    console.log(
      `✉️ Email sent successfully to ${userEmail} [ID: ${info.messageId}]`,
    );
    return true;
  } catch (error) {
    console.error("❌ Error sending OTP email:", error);
    throw error;
  }
};
