/*
 *    Copyright (c) 2016 Robert Cooper
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package net.kebernet.skillz.util;


import com.google.common.base.Joiner;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *  A class for converting between different concrete types.
 */
@ThreadSafe
@Singleton
public class Coercion {
    private static final Logger LOGGER = Logger.getLogger(Coercion.class.getCanonicalName());
    private static final ThreadLocal<String> DATE_OVERRIDE = new ThreadLocal<>();
    private static final Converter<Object, Object> PASSTHROUGH = source -> source;

    private final Map<Key, Converter> coercions = new ConcurrentHashMap<>();
    private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    { // Instance init
        Key key = new Key(String.class, String.class);
        coercions.put(key, new Converter<String, String>() {

            @Override
            public String convert(String source) {
                if (source == null || source.trim().isEmpty()) {
                    return null;
                } else {
                    return source;
                }
            }
        });
        key = new Key(String.class, Integer.class);
        coercions.put(key, new Converter<String, Integer>() {

            @Override
            public Integer convert(String source) {
                if (source == null || source.trim().isEmpty()) {
                    return null;
                } else {
                    return Integer.valueOf(source);
                }
            }
        });
        coercions.put(new Key(String.class, int.class), coercions.get(key));

        key = new Key(String.class, Boolean.class);
        coercions.put(key, new Converter<String, Boolean>(){

            @Override
            @SuppressFBWarnings("NP_BOOLEAN_RETURN_NULL")
            public Boolean convert(String source) {
                if(source == null || source.trim().isEmpty()){
                    return null;
                } else {
                    source = source.toLowerCase().trim();
                    if("yes".equals(source)){
                        source = "true";
                    } else if("no".equals(source)){
                        source = "false";
                    }
                    return Boolean.valueOf(source);
                }
            }
        });
        coercions.put(new Key(String.class, boolean.class), coercions.get(key));

        key = new Key(String.class, Long.class);
        coercions.put(key, new Converter<String, Long>(){

            @Override
            public Long convert(String source) {
                if(source == null || source.trim().isEmpty()){
                    return null;
                } else {
                    return Long.valueOf(source);
                }
            }
        });
        key = new Key(String.class, Double.class);
        coercions.put(key, new Converter<String, Double>() {

            @Override
            public Double convert(String source) {
                if (source == null || source.trim().isEmpty()) {
                    return null;
                } else {
                    return Double.valueOf(source);
                }
            }
        });
        coercions.put(new Key(String.class, double.class), coercions.get(key));

        coercions.put(new Key(String.class, Date.class), new Converter<String, Date>() {
            @Override
            public Date convert(String source) {
                if (source == null || source.trim().isEmpty()) {
                    return null;
                } else {
                    try {
                        //Create a new one every time for thread safety
                        return new SimpleDateFormat(getDatePattern()).parse(source);
                    } catch (ParseException e) {
                        return null;
                    }
                }
            }
        });

        coercions.put(new Key(Date.class, String.class), new Converter<Date, String>() {
            @Override
            public String convert(Date source) {
                if (source == null) {
                    return null;
                } else {
                    //Create a new one every time for thread safety
                    return new SimpleDateFormat(getDatePattern()).format(source);
                }
            }
        });

        coercions.put(new Key(Date.class, Long.class), new Converter<Date, Long>() {
            @Override
            public Long convert(Date source) {
                if (source == null) {
                    return null;
                } else {
                    return Long.valueOf(source.getTime());
                }
            }
        });

        coercions.put(new Key(Integer.class, Long.class), new Converter<Integer, Long>() {

            @Override
            public Long convert(Integer source) {
                if (source == null) {
                    return null;
                } else {
                    return source.longValue();
                }
            }
        });

        coercions.put(new Key(String.class, List.class), new Converter<String, List>(){

            @Override
            public List convert(String source) {
                if(source == null || source.trim().isEmpty()){
                    return null;
                }
                return Arrays.asList(source.split("\\|"));
            }
        });

        coercions.put(new Key(List.class, String.class), new Converter<List, String>() {

            @Override
            public String convert(List source) {
                if(source.isEmpty()){
                    return null;
                } else {
                    return Joiner.on("|").skipNulls().join(source);
                }
            }
        });
        Converter<Object, String> toStringConverter = source -> {
            if(source == null){
                return null;
            }
            return source.toString();
        };
        Converter<String, Long> longFromString = source -> source == null ? null : Long.valueOf(source);
        coercions.put(new Key(Integer.class, String.class), toStringConverter);
        coercions.put(new Key(Long.class, String.class), toStringConverter);
        coercions.put(new Key(Double.class, String.class), toStringConverter);
        coercions.put(new Key(Float.class, String.class), toStringConverter);
        coercions.put(new Key(Boolean.class, String.class), toStringConverter);
        coercions.put(new Key(String.class, Long.class), longFromString);
        coercions.put(new Key(int.class, Integer.class), PASSTHROUGH);
        coercions.put(new Key(long.class, Long.class), PASSTHROUGH);
        coercions.put(new Key(float.class, Float.class), PASSTHROUGH);
        coercions.put(new Key(double.class, Double.class), PASSTHROUGH);
        coercions.put(new Key(byte.class, Byte.class), PASSTHROUGH);
        coercions.put(new Key(char.class, Character.class), PASSTHROUGH);
        coercions.put(new Key(boolean.class, Boolean.class), PASSTHROUGH);
        coercions.put(new Key(Integer.class, int.class), PASSTHROUGH);
        coercions.put(new Key(Long.class, long.class), PASSTHROUGH);
        coercions.put(new Key(Float.class, float.class), PASSTHROUGH);
        coercions.put(new Key(Double.class, double.class), PASSTHROUGH);
        coercions.put(new Key(Byte.class, byte.class), PASSTHROUGH);
        coercions.put(new Key(Character.class, char.class), PASSTHROUGH);
        coercions.put(new Key(Boolean.class, boolean.class), PASSTHROUGH);



    }

    @Inject
    public Coercion(){
        super();
    }


    public <S,D> void addConverter(Class<S> source, Class<D> destination, Converter<S,D> converter){
        if(this.coercions.put(new Key(source, destination), converter) != null){
            LOGGER.warning("Replacing coercion from "+source.getCanonicalName() +" to " + destination.getCanonicalName());
        }
    }

    /**
     * Coerce from one type to another
     * @param source the source value to coerce
     * @param destination the destination type
     * @param <S> the source type
     * @param <D> the destination type
     * @return A destination instance or null;
     */
    public <S,D> D coerce(@Nullable S source, @Nonnull Class<D> destination){
        if(source == null){
            return null;
        } else {
            return coerce(source.getClass(), source, destination);
        }
    }

    /**
     * Coerce from one type to another
     * @param sourceClass The class to treat the source as.
     * @param source the source value to coerce
     * @param destination the destination type
     * @param <S> the source type
     * @param <D> the destination type
     * @return A destination instance or null;
     */
    @SuppressWarnings("unchecked")
    public <S,D> D coerce(@Nonnull Class<?> sourceClass, @Nullable S source, @Nonnull Class<D> destination){
        checkNotNull(destination, "You must provide a target class");
        destination = (Class<D>) noPrimitives(destination);
        D value = null;
        Converter<S, D> convert = null;
        boolean hasConverter = source != null && (convert = coercions.get(new Key(sourceClass, destination))) != null;

        if(source == null){
            return null;
        } else if(!hasConverter && sourceClass.isAssignableFrom(destination)) {
            value = (D) source;
        }else if(destination.isEnum()) {
            if(source instanceof Number) {
                return destination.getEnumConstants()[((Number)source).intValue()];
            } else {
                for(D d : destination.getEnumConstants()){
                    if(d.toString().equalsIgnoreCase(source.toString())) {
                        return d;
                    }
                }
                throw new RuntimeException("Couldn't find enum value for "+source+" on type "+destination.getCanonicalName());
            }
        } else {
            if(String.class.equals(destination) && convert == null){
                return (D) source.toString();
            }
            checkNotNull(convert, "Could not find a converter to go from "+sourceClass+" to "+destination);
            return convert.convert(source);
        }
        return value;
    }

    private  Class<?> noPrimitives(Class<?> destination) {
        if(!destination.isPrimitive()){
            return destination;
        }
        if(destination == Long.TYPE){
            return Long.class;
        }
        if(destination == Integer.TYPE){
            return Integer.class;
        }
        if(destination == Boolean.TYPE){
            return Boolean.class;
        }
        if(destination == Character.TYPE){
            return Character.class;
        }
        if(destination == Byte.TYPE){
            return Byte.class;
        }
        if(destination == Float.TYPE){
            return Float.class;
        }
        if(destination == Double.TYPE){
            return Double.class;
        }
        throw new RuntimeException("Unhandled primitive type "+destination.getCanonicalName());
    }

    public static String getDatePattern(){
        if(DATE_OVERRIDE.get() != null){
            return DATE_OVERRIDE.get();
        } else {
            return DATE_PATTERN;
        }
    }

    /**
     * Sets a *ThreadLocal* date format for use in coercing dates.
     * @param format The new DateFormat.
     */
    public static void setDateOverride(String format){
        if(format == null){
            DATE_OVERRIDE.remove();
        } else {
            DATE_OVERRIDE.set(format);
        }
    }

    /**
     * A simple marker to convert from one type to antoher
     * @param <S> Source type
     * @param <D> DestinationType
     */
    private interface Converter<S,D> {
        D convert(@Nullable S source);
    }

    /**
     * A key to look up converters with
     */
    private static class Key {
        final Class destination;
        final Class source;

        private Key(@Nonnull Class source, @Nonnull Class destination) {
            this.destination = destination;
            this.source = source;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Key key = (Key) o;

            if (!destination.equals(key.destination)) return false;
            if (!source.equals(key.source)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = destination.hashCode();
            result = 31 * result + source.hashCode();
            return result;
        }
    }
}