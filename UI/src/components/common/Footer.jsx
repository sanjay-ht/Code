// Author: Ankit Kumar Sharma
import React from "react";

const FooterComponent = () => {
  return (
    <div>
      <footer className="footer">
        <span>
          Hitachi Systems India Pvt. Ltd | All Right Reserved &copy;{" "}
          {new Date().getFullYear()}{" "}
        </span>
      </footer>
    </div>
  );
};
export default FooterComponent;
