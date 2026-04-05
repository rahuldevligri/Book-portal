package com.example.bookportal.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.Locale;
import java.util.List;

@Configuration
public class I18nConfig implements WebMvcConfigurer {
    private static final List<Locale> SUPPORTED_LOCALES = List.of(
            Locale.ENGLISH,
            Locale.forLanguageTag("ar"),
            Locale.forLanguageTag("es"),
            Locale.FRENCH);

    @Bean
    /**
     * Creates and configures the MessageSource bean for internationalization.
     * 
     * @return the configured MessageSource
     */
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setFallbackToSystemLocale(false);
        return messageSource;
    }

    @Bean
    /**
     * Creates and configures the LocaleResolver bean for locale management.
     * 
     * @return the configured LocaleResolver
     */
    public LocaleResolver localeResolver() {
        CookieLocaleResolver resolver = new CookieLocaleResolver("BOOK_PORTAL_LANG");
        resolver.setDefaultLocale(SUPPORTED_LOCALES.get(0));
        resolver.setCookiePath("/");
        resolver.setCookieHttpOnly(true);
        resolver.setCookieMaxAge(java.time.Duration.ofDays(30));
        return resolver;
    }

    @Bean
    /**
     * Creates and configures the LocaleChangeInterceptor bean to handle locale
     * changes.
     * 
     * @return the configured LocaleChangeInterceptor
     */
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang");
        return interceptor;
    }

    @Override
    /**
     * Adds the LocaleChangeInterceptor to the application's interceptor registry.
     * 
     * @param registry the InterceptorRegistry
     */
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }
}
