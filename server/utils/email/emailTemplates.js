/**
 * Generates the HTML template for the registration OTP email.
 * Theme: Vent Custom (Navy Blue, Cream, Orange)
 */
exports.getRegistrationOTPTemplate = (otpCode) => {
  return `
        <div style="font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; padding: 30px 20px; max-width: 600px; margin: auto; background-color: #ffffff;">

            <div style="border: 2px solid #AFBBF2; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 10px rgba(0, 51, 102, 0.05);">

                <div style="background-color: #003366; padding: 20px; text-align: center;">
                    <h2 style="color: #ffffff; margin: 0; font-size: 24px; letter-spacing: 1px;">Welcome to Vent</h2>
                </div>

                <div style="background-color: #FFF5E1; padding: 40px 20px; text-align: center;">
                    <p style="font-size: 16px; color: #003366; margin-top: 0; font-weight: 500;">Your 6-digit registration code is:</p>

                    <div style="background-color: #ffffff; padding: 20px; text-align: center; border-radius: 8px; margin: 25px auto; max-width: 300px; border: 1px dashed #003366;">
                        <h1 style="color: #FF6600; letter-spacing: 12px; margin: 0; font-size: 42px; font-weight: bold;">${otpCode}</h1>
                    </div>

                    <p style="font-size: 14px; color: #003366; opacity: 0.8; margin-bottom: 0;">This code will expire in 5 minutes.<br>Do not share it with anyone.</p>
                </div>

                <div style="background-color: #AFBBF2; padding: 12px; text-align: center;">
                    <p style="font-size: 12px; color: #003366; margin: 0; opacity: 0.7;">Secure Verification by Vent</p>
                </div>
            </div>

        </div>
    `;
};
