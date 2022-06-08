package upload;

import java.io.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;


public class QRCodeServlet {

    private static final long serialVersionUID = 1357779219336485986L;

    public static void main(String[] args) throws IOException {
        ByteArrayOutputStream out = QRCode.from("").to(ImageType.PNG).stream();
        new FileOutputStream("/Users/jeffrey/Desktop/test.png").write(out.toByteArray());

    }

}
