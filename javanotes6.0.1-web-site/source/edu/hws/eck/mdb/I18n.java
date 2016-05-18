package edu.hws.eck.mdb;

import java.util.Locale;
import java.util.ResourceBundle;
import java.text.MessageFormat;

/**
 * This class provides some support for Internationalization, making it possible
 * to easily get a String corresponding to a key in a properties file, using the
 * method I18n.tr().  A method is also provided to change the locale.
 */
public class I18n {
   
   /**
    * The base name for the properties files for this program.  The default
    * properties file name is strings.properties in the directory edu/hws/eck/mdb.
    * Property files for other locales would have the form strings_xx.properties
    * where xx is the code for the locale.  To use this file in another program,
    * only the propertyFileName has to be changed.
    */
   private final static String propertyFileName = "edu.hws.eck.mdb.strings";
   
   /**
    * If locale is null, the default locale is used.  A non-null value can be
    * set by calling setLocale() or setLocaleName() to use a non-default locale.
    */
   private static Locale locale;
   
   /**
    * The resource bundle that is created from the properties file.
    */
   private static ResourceBundle translations;
   
   /**
    * Look up a key string in the properties file to find its translation.  If no
    * translation is found or any other error occurs, the key string is returned
    * unmodified.
    * @param key the key string.
    * @param args if the value string associated with the key contains placeholders 
    *   {0}, {1}, {2}, ...,  then the values of the optional args parameters are substituted
    *   for the placeholders. 
    * @return the value associated to key in the properties files, with placeholders replaced
    *   by args if appropriate; if any error occurs, the key string is returned.
    */
   public static String tr(String key, Object... args) {
      if (translations == null)
         loadStrings();
      if (translations == null)
         return key;
      try {
         String str = translations.getString(key);
         return MessageFormat.format(str,args);
      }
      catch (Exception e) {
         return key;
      }
   }
   
   /**
    * Same as tr(), except that when an error occurs, the return value is null.
    */
   public static String trIfFound(String key, Object... args) {
      if (translations == null)
         loadStrings();
      if (translations == null)
         return null;
      try {
         String str = translations.getString(key);
         return MessageFormat.format(str,args);
      }
      catch (Exception e) {
         return null;
      }
   }
   
   /**
    * Specifies a locale to use for translations in future calls to I18n.tr().
    * (This does not, of course, affect any strings that have already been
    * translated already.)
    * @param newLocale the locale to use, or null to use the default locale
    */
   public static void setLocale(Locale newLocale) {
      if ( (locale == null && newLocale != null )
            || (locale != null && !locale.equals(newLocale)) ) {
         locale = newLocale;
         translations = null; // forces loading of a new resource bundle
      }
   }
   
   /**
    * Convenience method calls setLocale(new Locale(localeLanguageCode)).
    * This would be used, for example, as I18n.setLanguage("fr");
    * @param localeLanguageCode the two-letter language code for the locale
    */
   public static void setLanguage(String localeLanguageCode) {
      setLocale(new Locale(localeLanguageCode));
   }
   
   /**
    * Used for loading the resource bundle.  If it can't be loaded, then
    * the bundle will be null after this method returns.
    */
   private static void loadStrings() {
      if (locale != null) {
         try {
            translations = ResourceBundle.getBundle(propertyFileName, locale);
            return;
         }
         catch (Exception e) {
         }
      }
      try {
         translations = ResourceBundle.getBundle(propertyFileName);
      }
      catch (Exception e) {
         translations = null;
      }
   }
   
   /**
    * Declaring a private constructor makes sure that no one will be
    * able to create instances of this class.  Everything in this
    * class is static, so there is no reason to create an I18n object.
    */
   private I18n() {
   }
   
}
