package com.textaura.social;

import java.util.HashMap;
import java.util.Map;

final class Texts {
    private static final Map<String,String[]> DATA = new HashMap<>();
    static {
        put("tag","Donnez du style à vos publications ✨","Give your posts some style ✨","Da estilo a tus publicaciones ✨","Verleihe deinen Beiträgen Stil ✨","أضف لمسة جميلة إلى منشوراتك ✨");
        put("your_text","Votre texte","Your text","Tu texto","Dein Text","نصك");
        put("hint","Écrivez ou collez votre publication ici…","Write or paste your post here…","Escribe o pega tu publicación aquí…","Schreibe oder füge deinen Beitrag hier ein…","اكتب أو الصق منشورك هنا…");
        put("tip","Sélectionnez un passage, puis touchez un outil. Sans sélection, l’action s’applique à tout le texte.","Select a passage, then tap a tool. With no selection, it applies to all text.","Selecciona un fragmento y toca una herramienta. Sin selección, se aplica a todo el texto.","Markiere einen Abschnitt und tippe auf ein Werkzeug. Ohne Auswahl gilt es für den ganzen Text.","حدّد مقطعًا ثم اختر أداة. من دون تحديد، تُطبّق على النص كله.");
        put("styles","Styles rapides","Quick styles","Estilos rápidos","Schnelle Stile","أنماط سريعة");
        put("decor","Décorations","Decorations","Decoraciones","Dekorationen","زخارف");
        put("preview","Aperçu","Preview","Vista previa","Vorschau","معاينة");
        put("preview_empty","Votre texte stylisé apparaîtra ici.","Your styled text will appear here.","Tu texto con estilo aparecerá aquí.","Dein formatierter Text erscheint hier.","سيظهر النص المنسّق هنا.");
        put("copy","Copier le texte","Copy text","Copiar texto","Text kopieren","نسخ النص");
        put("share","Partager le texte","Share text","Compartir texto","Text teilen","مشاركة النص");
        put("share_image","Partager en image","Share as image","Compartir como imagen","Als Bild teilen","مشاركة كصورة");
        put("emoji","Emojis","Emojis","Emojis","Emojis","رموز تعبيرية");
        put("clear","Effacer","Clear","Borrar","Löschen","مسح");
        put("undo","Annuler","Undo","Deshacer","Rückgängig","تراجع");
        put("redo","Rétablir","Redo","Rehacer","Wiederholen","إعادة");
        put("hide_keyboard","Masquer le clavier","Hide keyboard","Ocultar teclado","Tastatur ausblenden","إخفاء لوحة المفاتيح");
        put("selection_saved","Sélection conservée","Selection preserved","Selección conservada","Auswahl gespeichert","تم حفظ التحديد");
        put("emoji_added","Emoji déjà ajouté","Emoji already added","Emoji ya añadido","Emoji bereits hinzugefügt","تمت إضافة الرمز مسبقًا");
        put("chars","caractères","characters","caracteres","Zeichen","حرفًا");
        put("empty","Écrivez d’abord un texte.","Write some text first.","Primero escribe un texto.","Schreibe zuerst einen Text.","اكتب نصًا أولًا.");
        put("copied","Texte copié","Text copied","Texto copiado","Text kopiert","تم نسخ النص");
        put("share_with","Partager avec…","Share with…","Compartir con…","Teilen mit…","مشاركة عبر…");
        put("privacy","🔒 Hors ligne, sans compte. Vos textes restent sur votre téléphone.","🔒 Offline, no account. Your texts stay on your phone.","🔒 Sin conexión ni cuenta. Tus textos permanecen en tu teléfono.","🔒 Offline, ohne Konto. Deine Texte bleiben auf deinem Telefon.","🔒 يعمل دون إنترنت أو حساب. تبقى نصوصك على هاتفك.");
        put("app_missing","Application non installée : ouverture du menu de partage.","App not installed: opening the share menu.","Aplicación no instalada: se abre el menú de compartir.","App nicht installiert: Das Teilen-Menü wird geöffnet.","التطبيق غير مثبت: سيتم فتح قائمة المشاركة.");
        put("rich_note","Les couleurs, les alignements, les puces, le surligneur et le soulignement continu sont conservés dans l’image. Le texte simple des réseaux sociaux ne prend pas en charge les couleurs ni l’alignement.","Colors, alignment, lists, highlighting and continuous underlining are preserved in the image. Plain social-media text does not support colors or alignment.","Los colores, la alineación, las listas, el resaltado y el subrayado continuo se conservan en la imagen. El texto simple de las redes no admite colores ni alineación.","Farben, Ausrichtung, Listen, Markierung und durchgehende Unterstreichung bleiben im Bild erhalten. Einfacher Social-Media-Text unterstützt keine Farben oder Ausrichtung.","تُحفظ الألوان والمحاذاة والقوائم والتظليل والخط المتصل في الصورة. النص العادي في الشبكات الاجتماعية لا يدعم الألوان أو المحاذاة.");

        put("normal","Normal","Normal","Normal","Normal","عادي");
        put("bold","Gras","Bold","Negrita","Fett","عريض");
        put("italic","Italique","Italic","Cursiva","Kursiv","مائل");
        put("bolditalic","Gras italique","Bold italic","Negrita cursiva","Fett kursiv","عريض مائل");
        put("serif","Gras serif","Serif bold","Negrita serif","Serifenfett","عريض مزخرف");
        put("mono","Monospace","Monospace","Monoespaciado","Monospace","أحادي المسافة");
        put("double","Double trait","Double-struck","Doble trazo","Doppelstrich","خط مزدوج");
        put("script","Cursif","Script","Caligrafía","Schreibschrift","خط يدوي");
        put("small","Petites capitales","Small caps","Versalitas","Kapitälchen","حروف صغيرة كبيرة");
        put("full","Pleine largeur","Full width","Ancho completo","Vollbreite","عرض كامل");
        put("circle","Cerclé","Circled","Círculos","Eingekreist","داخل دوائر");
        put("underline","Souligné continu","Continuous underline","Subrayado continuo","Durchgehend unterstrichen","تسطير متصل");
        put("strike","Barré","Strikethrough","Tachado","Durchgestrichen","يتوسطه خط");
        put("over","Ligne supérieure","Overline","Línea superior","Überstrichen","فوقه خط");
        put("highlight","Surligneur","Highlight","Resaltador","Textmarker","تمييز");
        put("spark","Étincelles","Sparkles","Destellos","Funkeln","بريق");
        put("heart","Cœurs","Hearts","Corazones","Herzen","قلوب");
        put("frame","Encadré","Framed","Enmarcado","Eingerahmt","داخل إطار");

        put("formats","Casse, taille et formats","Case, size and formats","Mayúsculas y formatos","Großschreibung und Formate","حالة الأحرف والتنسيقات");
        put("casing","Casse et majuscules","Casing","Mayúsculas y minúsculas","Groß- und Kleinschreibung","حالة الأحرف");
        put("naming","Séparateurs informatiques","Naming conventions","Convenciones de nombres","Benennungskonventionen","تنسيقات التسمية");
        put("cleanup","Nettoyage du texte","Text cleanup","Limpieza de texto","Textbereinigung","تنظيف النص");
        put("sentence","Phrase","Sentence case","Primera mayúscula","Satzschreibung","حرف أول كبير");
        put("lower","minuscules","lowercase","minúsculas","kleinbuchstaben","أحرف صغيرة");
        put("upper","MAJUSCULES","UPPERCASE","MAYÚSCULAS","GROSSBUCHSTABEN","أحرف كبيرة");
        put("title","Nom Propre","Title Case","Tipo Título","Jedes Wort Groß","أول كل كلمة كبير");
        put("toggle","Inverser la casse","Toggle Case","Invertir mayúsculas","Groß/Klein umkehren","عكس الحالة");
        put("sponge","Aléatoire","SpongeBob Case","Alternado","Abwechselnd","حالة عشوائية");
        put("snake","snake_case","snake_case","snake_case","snake_case","snake_case");
        put("kebab","kebab-case","kebab-case","kebab-case","kebab-case","kebab-case");
        put("camel","camelCase","camelCase","camelCase","camelCase","camelCase");
        put("pascal","PascalCase","PascalCase","PascalCase","PascalCase","PascalCase");
        put("no_accents","Sans accents","Remove accents","Sin acentos","Ohne Akzente","من دون حركات");
        put("no_spaces","Sans espaces","Remove spaces","Sin espacios","Ohne Leerzeichen","من دون مسافات");

        put("colors","Couleurs et surlignage","Colors and highlighting","Colores y resaltado","Farben und Markierung","الألوان والتظليل");
        put("text_color","Couleur du texte","Text color","Color del texto","Textfarbe","لون النص");
        put("highlight_color","Surligneur couleur","Highlight color","Color de resaltado","Markierungsfarbe","لون التظليل");
        put("underline_color","Soulignement couleur","Underline color","Color de subrayado","Unterstreichungsfarbe","لون التسطير");
        put("clear_colors","Retirer les couleurs","Remove colors","Quitar colores","Farben entfernen","إزالة الألوان");
        put("color_title","Choisir une couleur","Choose a color","Elegir un color","Farbe wählen","اختر لونًا");

        put("paragraph_tools","Alignement, paragraphes et listes","Alignment, paragraphs and lists","Alineación, párrafos y listas","Ausrichtung, Absätze und Listen","المحاذاة والفقرات والقوائم");
        put("alignment","Alignement du texte","Text alignment","Alineación del texto","Textausrichtung","محاذاة النص");
        put("align_left","Aligner à gauche","Align left","Alinear a la izquierda","Linksbündig","محاذاة لليسار");
        put("align_center","Centré","Centered","Centrado","Zentriert","توسيط");
        put("align_right","Aligner à droite","Align right","Alinear a la derecha","Rechtsbündig","محاذاة لليمين");
        put("justify","Justifier le texte","Justify text","Justificar texto","Blocksatz","ضبط النص");
        put("lists","Puces et listes","Bullets and lists","Viñetas y listas","Aufzählungen und Listen","التعداد والقوائم");
        put("list_number","Numérique","Numbered","Numérica","Nummeriert","رقمية");
        put("list_letter","Lettres","Letters","Letras","Buchstaben","حروف");
        put("list_roman","Chiffres romains","Roman numerals","Números romanos","Römische Zahlen","أرقام رومانية");
        put("list_bullet","Puce ronde","Round bullet","Viñeta redonda","Runder Punkt","نقطة");
        put("list_dash","Tiret","Dash","Guion","Gedankenstrich","شرطة");
        put("list_check","Coche","Check mark","Marca de verificación","Häkchen","علامة صح");
        put("list_arrow","Flèche","Arrow","Flecha","Pfeil","سهم");
        put("list_star","Étoile","Star","Estrella","Stern","نجمة");
        put("list_diamond","Losange","Diamond","Rombo","Raute","معين");
        put("list_remove","Retirer les puces","Remove bullets","Quitar viñetas","Aufzählung entfernen","إزالة التعداد");

        put("language","Langue","Language","Idioma","Sprache","اللغة");
        put("emoji_title","Bibliothèque d’emojis","Emoji library","Biblioteca de emojis","Emoji-Bibliothek","مكتبة الرموز التعبيرية");
        put("emoji_smileys","Smileys et émotions","Smileys & Emotion","Caritas y emociones","Smileys & Emotionen","الوجوه والمشاعر");
        put("emoji_people","Personnes et corps","People & Body","Personas y cuerpo","Menschen & Körper","الأشخاص والجسم");
        put("emoji_nature","Animaux et nature","Animals & Nature","Animales y naturaleza","Tiere & Natur","الحيوانات والطبيعة");
        put("emoji_food","Nourriture et boissons","Food & Drink","Comida y bebida","Essen & Trinken","الطعام والشراب");
        put("emoji_activities","Activités","Activities","Actividades","Aktivitäten","الأنشطة");
        put("emoji_travel","Voyages et lieux","Travel & Places","Viajes y lugares","Reisen & Orte","السفر والأماكن");
        put("emoji_objects","Objets","Objects","Objetos","Objekte","الأشياء");
        put("emoji_symbols","Symboles","Symbols","Símbolos","Symbole","الرموز");
        put("emoji_flags","Drapeaux","Flags","Banderas","Flaggen","الأعلام");
        put("close","Fermer","Close","Cerrar","Schließen","إغلاق");
    }

    private Texts() {}
    private static void put(String key,String fr,String en,String es,String de,String ar){DATA.put(key,new String[]{fr,en,es,de,ar});}
    static String get(String key,int languageIndex){String[] values=DATA.get(key);return values==null?key:values[Math.max(0,Math.min(languageIndex,values.length-1))];}
}
