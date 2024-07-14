package com.example.webapp.util;

import com.example.webapp.entity.Usuario;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Component
public class Correo {

    @Autowired
    private JavaMailSender mailSender;

    public boolean EnviarCorreo(String subject, String html, Usuario obj) {
        boolean estado = false;

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
            helper.setTo(obj.getCorreo());
            helper.setText(html, true);
            helper.setSubject(subject);
            mailSender.send(message);
            estado = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return estado;
    }

    public String construirCuerpo(Usuario obj) {

        String cuerpo = " <html lang='en'>\n"
                + "<head>\n"
                + "    <meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />\n"
                + "</head>\n"
                + "<body>\n"
                + "    <center>\n"
                + "        <div style='text-align: left; color: rgba(15, 153, 171, 1); width: 670px; box-shadow: 0 4px 8px 0 rgba(0,0,0,0.2);transition: 0.3s'>\n"
                + "            <div style='padding: 2px 16px;'>\n"
                + "                <div style='background:  rgba(15, 153, 171, 1) !important; padding: 10px; '>\n"
                + "                    <table>\n"
                + "                        <tr>\n"
                + "                            <td>\n"
                + "                                <span style='font-size: 24px; \n"
                + "                                font-family:LucidaGrande,tahoma,verdana,arial,sans-serif;\n"
                + "                                color: white;'>PildoPharm</span>\n"
                + "                            </td>\n"
                + "                        </tr>\n"
                + "                    </table>\n"
                + "                </div>\n"
                + "       \n"
                + "                <p>Hola, @@_NOMBRES_COMPLETOS_@@:</p>\n"
                + "                <span style='text-align: justify;'>Te damos la bienvenida a nuestra familia PildoPharm,</span><br />\n"
                + "                </span>\n"
                + "                 <p style='font-style: italic;'>Nota: Recuerda que para acceder a nuestra plataforma debes hacerlo con las siguientes credenciales, se le recomienda cambiar de contraseña por una nueva.</p>\n"
                + "                <p>Correo: <span>@@_CORREO_@@</span></p>\n"
                + "                <p>Contraseña: <span>@@_PASSWORD_@@</span></p>\n"
                + "            </div>\n"
                + "        </div>\n"
                + "    </center>\n"
                + "</body>\n"
                + "</html>;";
        cuerpo = cuerpo.replaceAll("@@_NOMBRES_COMPLETOS_@@", obj.getNombres() + " " + obj.getApellidos());
        cuerpo = cuerpo.replaceAll("@@_CORREO_@@", obj.getCorreo());
        cuerpo = cuerpo.replaceAll("@@_PASSWORD_@@", "" + obj.getDni());
        return cuerpo;
    }

    public String construirCuerpoRecuperarPassword(Usuario obj) {
        String urlBase = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        String url = urlBase + "/reestablecer/password?token=" + obj.getToken_recuperacion();

        String cuerpo = " <html lang='en'>\n"
                + "<head>\n"
                + "    <meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />\n"
                + "</head>\n"
                + "<body>\n"
                + "    <center>\n"
                + "        <div style='width: 600px; text-align: left; color: #333; font-family: Arial, sans-serif; font-size: 16px;'>\n"
                + "            <div style='background-color: #0f99ab; padding: 20px;'>\n"
                + "                <img src='https://i.postimg.cc/xC1W4t9j/artwork.png' alt='Logo' style='width: 100px; height: auto;' />\n"
                + "                <h1 style='color: #fff; font-size: 24px;'>REESTABLECER CONTRASEÑA: PildoPharm</h1>\n"
                + "            </div>\n"
                + "            <div style='padding: 20px; background-color: #f4f4f4;'>\n"
                + "                <p>Hola, @@_NOMBRES_COMPLETOS_@@:</p>\n"
                + "                <p>Recibimos una solicitud para reestablecer tu contraseña.<br />\n"
                + "                Ingresa al siguiente link proporcionado:</p>\n"
                + "                <p style='text-align: center; margin: 20px 0;'>\n"
                + "                    <a href='@@_URL_@@' style='background-color: #0f99ab; color: #fff; padding: 10px 20px; text-decoration: none; border-radius: 5px;'>Link de recuperación</a>\n"
                + "                </p>\n"
                + "                <p style='font-style: italic;'>Nota: Recuerda que este url solo es válido por los próximos @@_MINUTES_RECUPERAR_@@ minutos desde el envío de este correo.</p>\n"
                + "                <p><strong>¿No solicitaste este cambio?</strong></p>\n"
                + "                <p>Si no solicitaste reestablecer tu contraseña, ignora este mensaje.</p>\n"
                + "            </div>\n"
                + "        </div>\n"
                + "    </center>\n"
                + "</body>\n"
                + "</html>";

        cuerpo = cuerpo.replaceAll("@@_URL_@@", url);
        cuerpo = cuerpo.replaceAll("@@_NOMBRES_COMPLETOS_@@", obj.getNombres() + " " + obj.getApellidos());
        cuerpo = cuerpo.replaceAll("@@_MINUTES_RECUPERAR_@@", "2"); // 10 minutos

        return cuerpo;
    }



    public String construirCuerpoActivarCuenta(Usuario obj) {
        String urlBase = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        String url = urlBase + "/reestablecer/activar?token=" + obj.getToken_recuperacion();

        String cuerpo = " <html lang='en'>\n"
                + "<head>\n"
                + "    <meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />\n"
                + "</head>\n"
                + "<body>\n"
                + "    <center>\n"
                + "        <div style='width: 600px; text-align: left; color: #333; font-family: Arial, sans-serif; font-size: 16px;'>\n"
                + "            <div style='background-color: #0f99ab; padding: 20px;'>\n"
                + "                <img src='https://www.example.com/logo.png' alt='Logo' style='width: 100px; height: auto;' />\n"
                + "                <h1 style='color: #fff; font-size: 24px;'>ACTIVAR CUENTA: PildoPharm</h1>\n"
                + "            </div>\n"
                + "            <div style='padding: 20px; background-color: #f4f4f4;'>\n"
                + "                <p>Hola, @@_NOMBRES_COMPLETOS_@@:</p>\n"
                + "                <p>Recibimos una solicitud para activar tu cuenta.<br />\n"
                + "                Ingresa al siguiente link proporcionado para su activación:</p>\n"
                + "                <p style='text-align: center; margin: 20px 0;'>\n"
                + "                    <a href='@@_URL_@@' style='background-color: #0f99ab; color: #fff; padding: 10px 20px; text-decoration: none; border-radius: 5px;'>Link de activación</a>\n"
                + "                </p>\n"
                + "                <p style='font-style: italic;'>Nota: Recuerda que este url solo es válido por una única vez.</p>\n"
                + "                <p>Bienvenido a la familia PildoPharm.</p>\n"
                + "            </div>\n"
                + "        </div>\n"
                + "    </center>\n"
                + "</body>\n"
                + "</html>";

        cuerpo = cuerpo.replaceAll("@@_URL_@@", url);
        cuerpo = cuerpo.replaceAll("@@_NOMBRES_COMPLETOS_@@", obj.getNombres() + " " + obj.getApellidos());

        return cuerpo;
    }

}
