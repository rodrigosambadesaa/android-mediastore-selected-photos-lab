# Enunciado didáctico reconstruido — MediaStore y acceso a fotos seleccionadas

Laboratorio Android centrado en el modelo moderno de privacidad para contenido multimedia, especialmente **Selected Photos Access** en Android 14+.

La aplicación debe:

- solicitar `READ_MEDIA_VISUAL_USER_SELECTED` junto con los permisos de imágenes/vídeos cuando corresponda;
- consultar mediante `MediaStore` únicamente el contenido al que la aplicación tenga acceso efectivo;
- distinguir entre acceso completo, acceso a selección del usuario y ausencia de permiso;
- demostrar como alternativa el **Photo Picker** del sistema, que permite seleccionar contenido sin conceder acceso general a toda la biblioteca;
- adaptarse al comportamiento de las distintas versiones Android mediante comprobaciones de API.

No existe una base de datos asociada.
