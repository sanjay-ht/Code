import React, { useState, useRef, useEffect } from 'react';
import './OtpVerification.css'; // Import custom CSS for styling

const OtpVerification = ({
  onOtpInput,
  timerCount,
  dialogOpen,
  email,
  handleClickResendOtp,
}) => {
  const [otp, setOtp] = useState(['', '', '', '', '', '']); // Array of empty strings for 6 OTP inputs
  const [timer, setTimer] = useState(timerCount);
  const [isResendDisabled, setIsResendDisabled] = useState(true);

  const inputRefs = useRef([]);

  useEffect(() => {
    if (inputRefs.current[0]) {
      inputRefs.current[0].focus();
    }
  }, []);

  useEffect(() => {
    let interval = null;

    if (timer > 0) {
      interval = setInterval(() => {
        setTimer((prevTimer) => prevTimer - 1);
      }, 1000);
    } else {
      setIsResendDisabled(false);
    }

    return () => {
      if (interval) clearInterval(interval);
    };
  }, [timer]);

  const handleChange = (index, e) => {
    const value = e.target.value;
    if (isNaN(value)) return; // Only allow numbers

    const newOtp = [...otp];
    newOtp[index] = value.substring(value.length - 1);
    setOtp(newOtp);

    // Move to next input if the current field is filled
    if (value && index < otp.length - 1 && inputRefs.current[index + 1]) {
      inputRefs.current[index + 1].focus();
    }
  };

  const handlePaste = (e) => {
    const pastedValue = e.clipboardData.getData('Text');
  
    // Only proceed if it's a 6-digit number
    if (pastedValue.length === 6 && !isNaN(pastedValue)) {
      // Update OTP state with 6 digits
      setOtp(pastedValue.split(''));
    }
  };

  const handleClick = (index) => {
    inputRefs.current[index].setSelectionRange(0, 1);

    if (index > 0 && !otp[index - 1]) {
      inputRefs.current[otp.indexOf('')].focus(); // Optional: Move to the first empty input
    }
  };

  const handleKeyDown = (index, e) => {
    if (
      e.key === 'Backspace' &&
      !otp[index] &&
      index > 0 &&
      inputRefs.current[index - 1]
    ) {
      // Move focus to the previous input field on backspace
      inputRefs.current[index - 1].focus();
    }
  };

  const handleValidate = () => {
    const combinedOtp = otp.join('');
    if (combinedOtp.length === otp.length) {
      // Call validation logic or API for OTP verification here
      alert(`OTP Submitted: ${combinedOtp}`);
      onOtpInput(combinedOtp);

    }
  };

  return (
    <div className={`otp-dialog ${dialogOpen ? 'open' : ''}`}>
      <div className="otp-dialog-content">
        <h3 className="otp-dialog-title">OTP Verification</h3>
        <div className="otp-dialog-body">
          <p className="otp-dialog-text">Enter OTP sent to {email}</p>
          <div className="otp-input">
            {otp.map((value, index) => (
              <input
                key={index}
                type="text"
                ref={(input) => (inputRefs.current[index] = input)}
                value={value}
                onChange={(e) => handleChange(index, e)}
                onClick={() => handleClick(index)}
                onKeyDown={(e) => handleKeyDown(index, e)}
                onPaste={handlePaste}
                className="otpInput"
                maxLength={1} // Allow only one character per input
              />
            ))}
          </div>
          <div className="resend-section">
            <p>Didn't receive OTP?</p>
            {timer > 0  ? (
              <p className="timer-text">
                <strong>Resend </strong>{timer} seconds
              </p>
            ) : (
              <button
                className="resend-btn"
                onClick={() => {
                  handleClickResendOtp(email);
                  setTimer(timerCount);
                }}
                disabled={isResendDisabled}
              >
                Resend
              </button>
            )}
          </div>
          <button className="verify-btn" onClick={handleValidate}>
            Verify & Proceed
          </button>
        </div>
      </div>
    </div>
  );
};

export default OtpVerification;
