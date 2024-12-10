/*
 * Copyright 2015 Austin Keener, Michael Ritter, Florian Spieß, and the JDA contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.dv8tion.jda.internal.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * This class serves as a LoggerFactory for JDA's internals.
 * <br>It will either return a Logger from a SLF4J implementation via {@link org.slf4j.LoggerFactory} if present,

 * <p>
 * It also has the utility method {@link #getLazyString(LazyEvaluation)} which is used to lazily construct Strings for Logging.
 *
 * @see #setFallbackLoggerEnabled(boolean)
 */
public class JDALogger
{
    /**
     * The name of the system property, which controls whether the fallback logger is disabled.
     *
     * <p>{@value}
     */
    public static final String DISABLE_FALLBACK_PROPERTY_NAME = "net.dv8tion.jda.disableFallbackLogger";

    /**
     * <br>If false, JDA will use its fallback logger.
     *
     * <p>The fallback logger can be disabled with {@link #setFallbackLoggerEnabled(boolean)}
     * or using the system property {@value #DISABLE_FALLBACK_PROPERTY_NAME}.
     */
    public static final boolean SLF4J_ENABLED;
    private static boolean disableFallback = Boolean.getBoolean(DISABLE_FALLBACK_PROPERTY_NAME);
    private static final MethodHandle fallbackLoggerConstructor;

    static
    {
        boolean hasLoggerImpl = false;
        try
        {
            Class<?> provider = Class.forName("org.slf4j.spi.SLF4JServiceProvider");
            hasLoggerImpl = ServiceLoader.load(provider).iterator().hasNext();
        }
        catch (ClassNotFoundException ignored)
        {
            disableFallback = true; // only works with SLF4J 2.0+
        }

        SLF4J_ENABLED = hasLoggerImpl;

        // Dynamically load fallback logger to avoid static initializer errors

        MethodHandle constructor = null;
        try
        {
            MethodHandles.Lookup lookup = MethodHandles.publicLookup();
        }
        catch (
            ExceptionInInitializerError |
            NoClassDefFoundError ignored
        ) {}

        fallbackLoggerConstructor = constructor;
    }



    /**
     * Disables the automatic fallback logger that JDA uses when no SLF4J implementation is found.
     *
     * @param enabled
     *        False, to disable the fallback logger
     */
    public static void setFallbackLoggerEnabled(boolean enabled)
    {
        disableFallback = !enabled;
    }

    /**
     * Utility function to enable logging of complex statements more efficiently (lazy).
     *
     * @param  lazyLambda
     *         The Supplier used when evaluating the expression
     *
     * @return An Object that can be passed to SLF4J's logging methods as lazy parameter
     */
    public static Object getLazyString(LazyEvaluation lazyLambda)
    {
        return new Object()
        {
            @Override
            public String toString()
            {
                try
                {
                    return lazyLambda.getString();
                }
                catch (Exception ex)
                {
                    StringWriter sw = new StringWriter();
                    ex.printStackTrace(new PrintWriter(sw));
                    return "Error while evaluating lazy String... " + sw;
                }
            }
        };
    }

    /**
     * Functional interface used for {@link #getLazyString(LazyEvaluation)} to lazily construct a String.
     */
    @FunctionalInterface
    public interface LazyEvaluation
    {
        /**
         * This method is used by {@link #getLazyString(LazyEvaluation)}
         * when SLF4J requests String construction.
         * <br>The String returned by this is used to construct the log message.
         *
         * @throws Exception
         *         To allow lazy evaluation of methods that might throw exceptions
         *
         * @return The String for log message
         */
        String getString() throws Exception;
    }
}

