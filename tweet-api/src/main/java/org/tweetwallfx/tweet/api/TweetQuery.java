/*
 * The MIT License
 *
 * Copyright 2014-2018 TweetWallFX
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.tweetwallfx.tweet.api;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public final class TweetQuery {

    private String query = null;
    private String lang = null;
    private String locale = null;
    private Long maxId = null;
    private Integer count = null;
    private String since = null;
    private Long sinceId = null;
    private String until = null;
    private ResultType resultType = null;

    /**
     * Returns the specified query
     *
     * @return query
     */
    public String getQuery() {
        return query;
    }

    /**
     * Sets the query string. All query conditions are AND connected.
     *
     * @param queryConditions the query string
     */
    public void setQuery(final String... queryConditions) {
        this.query = Arrays.stream(queryConditions)
                .filter(s -> null != s && !s.isEmpty())
                .collect(Collectors.collectingAndThen(
                        Collectors.joining(" "),
                        s -> s.isEmpty() ? null : s));
    }

    /**
     * Sets the query string
     *
     * @param queryConditions the query conditions
     *
     * @return the instance
     *
     * @see #setQuery(java.lang.String...) for more details
     */
    public TweetQuery query(final String... queryConditions) {
        setQuery(queryConditions);
        return this;
    }

    /**
     * Returns the lang
     *
     * @return lang
     */
    public String getLang() {
        return lang;
    }

    /**
     * restricts tweets to the given language, given by an
     * <a href="http://en.wikipedia.org/wiki/ISO_639-1">ISO 639-1 code</a>
     *
     * @param lang an <a href="http://en.wikipedia.org/wiki/ISO_639-1">ISO 639-1
     * code</a>
     */
    public void setLang(String lang) {
        this.lang = lang;
    }

    /**
     * restricts tweets to the given language, given by an
     * <a href="http://en.wikipedia.org/wiki/ISO_639-1">ISO 639-1 code</a>
     *
     * @param lang an <a href="http://en.wikipedia.org/wiki/ISO_639-1">ISO 639-1
     * code</a>
     * @return the instance
     */
    public TweetQuery lang(String lang) {
        setLang(lang);
        return this;
    }

    /**
     * Returns the language of the query you are sending (only ja is currently
     * effective). This is intended for language-specific clients and the
     * default should work in the majority of cases.
     *
     * @return locale
     */
    public String getLocale() {
        return locale;
    }

    /**
     * Specify the language of the query you are sending (only ja is currently
     * effective). This is intended for language-specific clients and the
     * default should work in the majority of cases.
     *
     * @param locale the locale
     */
    public void setLocale(String locale) {
        this.locale = locale;
    }

    /**
     * Specify the language of the query you are sending (only ja is currently
     * effective). This is intended for language-specific clients and the
     * default should work in the majority of cases.
     *
     * @param locale the locale
     * @return the instance
     */
    public TweetQuery locale(String locale) {
        setLocale(locale);
        return this;
    }

    /**
     * Returns tweets with status ids less than the given id.
     *
     * @return maxId
     */
    public Long getMaxId() {
        return maxId;
    }

    /**
     * If specified, returns tweets with status ids less than the given id.
     *
     * @param maxId maxId
     */
    public void setMaxId(Long maxId) {
        this.maxId = maxId;
    }

    /**
     * If specified, returns tweets with status ids less than the given id.
     *
     * @param maxId maxId
     * @return this instance
     */
    public TweetQuery maxId(Long maxId) {
        setMaxId(maxId);
        return this;
    }

    /**
     * Returns the number of tweets to return per page, up to a max of 100.
     *
     * @return count
     */
    public Integer getCount() {
        return count;
    }

    /**
     * Sets the number of tweets to return per page, up to a max of 100.
     *
     * @param count the number of tweets to return per page
     */
    public void setCount(Integer count) {
        this.count = count;
    }

    /**
     * Sets the number of tweets to return per page, up to a max of 100.
     *
     * @param count the number of tweets to return per page
     * @return the instance
     */
    public TweetQuery count(Integer count) {
        setCount(count);
        return this;
    }

    /**
     * Returns tweets with since the given date. Date should be formatted as
     * YYYY-MM-DD
     *
     * @return since
     */
    public String getSince() {
        return since;
    }

    /**
     * If specified, returns tweets with since the given date. Date should be
     * formatted as YYYY-MM-DD
     *
     * @param since since
     */
    public void setSince(String since) {
        this.since = since;
    }

    /**
     * If specified, returns tweets with since the given date. Date should be
     * formatted as YYYY-MM-DD
     *
     * @param since since
     * @return since
     */
    public TweetQuery since(String since) {
        setSince(since);
        return this;
    }

    /**
     * returns sinceId
     *
     * @return sinceId
     */
    public Long getSinceId() {
        return sinceId;
    }

    /**
     * returns tweets with status ids greater than the given id.
     *
     * @param sinceId returns tweets with status ids greater than the given id
     */
    public void setSinceId(Long sinceId) {
        this.sinceId = sinceId;
    }

    /**
     * returns tweets with status ids greater than the given id.
     *
     * @param sinceId returns tweets with status ids greater than the given id
     * @return the instance
     */
    public TweetQuery sinceId(Long sinceId) {
        setSinceId(sinceId);
        return this;
    }

    /**
     * Returns until
     *
     * @return until
     */
    public String getUntil() {
        return until;
    }

    /**
     * If specified, returns tweets with generated before the given date. Date
     * should be formatted as YYYY-MM-DD
     *
     * @param until until
     */
    public void setUntil(String until) {
        this.until = until;
    }

    /**
     * If specified, returns tweets with generated before the given date. Date
     * should be formatted as YYYY-MM-DD
     *
     * @param until until
     * @return the instance
     */
    public TweetQuery until(String until) {
        setUntil(until);
        return this;
    }

    public enum ResultType {

        /**
         * popular: return only the most popular results in the response.
         */
        popular,
        /**
         * mixed: Include both popular and real time results in the response.
         */
        mixed,
        /**
         * recent: return only the most recent results in the response.
         */
        recent;
    }

    /**
     * Returns resultType
     *
     * @return the resultType
     */
    public ResultType getResultType() {
        return resultType;
    }

    /**
     * Default value is Query.MIXED if parameter not specified
     *
     * @param resultType Query.MIXED or Query.POPULAR or Query.RECENT
     */
    public void setResultType(ResultType resultType) {
        this.resultType = resultType;
    }

    /**
     * If specified, returns tweets included popular or real time or both in the
     * response
     *
     * @param resultType resultType
     * @return the instance
     */
    public TweetQuery resultType(ResultType resultType) {
        setResultType(resultType);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final TweetQuery query1 = (TweetQuery) o;

        return Objects.equals(count, query1.count)
                && Objects.equals(maxId, query1.maxId)
                && Objects.equals(sinceId, query1.sinceId)
                && Objects.equals(lang, query1.lang)
                && Objects.equals(locale, query1.locale)
                && Objects.equals(query, query1.query)
                && Objects.equals(resultType, query1.resultType)
                && Objects.equals(since, query1.since)
                && Objects.equals(until, query1.until);
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + Objects.hashCode(query);
        result = 31 * result + Objects.hashCode(lang);
        result = 31 * result + Objects.hashCode(locale);
        result = 31 * result + Objects.hashCode(maxId);
        result = 31 * result + Objects.hashCode(count);
        result = 31 * result + Objects.hashCode(since);
        result = 31 * result + Objects.hashCode(sinceId);
        result = 31 * result + Objects.hashCode(until);
        result = 31 * result + Objects.hashCode(resultType);
        return result;
    }

    @Override
    public String toString() {
        return "Query{"
                + "query='" + query + '\''
                + ", lang='" + lang + '\''
                + ", locale='" + locale + '\''
                + ", maxId=" + maxId
                + ", count=" + count
                + ", since='" + since + '\''
                + ", sinceId=" + sinceId
                + ", until='" + until + '\''
                + ", resultType='" + resultType + '\''
                + '}';
    }

    public static String queryOR(final String... conditions) {
        return Arrays.stream(conditions)
                .filter(s -> null != s && !s.isEmpty())
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        l -> l.isEmpty()
                        ? null
                        : 1 == l.size()
                        ? l.get(0)
                        : l.stream().collect(Collectors.joining(" OR ", "( ", " )"))));
    }

    public static String queryFrom(final String from) {
        return Optional.ofNullable(from)
                .map(s -> "from:" + from)
                .orElse(null);
    }

    public static String queryTo(final String to) {
        return Optional.ofNullable(to)
                .map(s -> "to:" + to)
                .orElse(null);
    }

    public static String queryFilterMedia() {
        return "Filter:media";
    }

    public static String queryFilterLinks() {
        return "Filter:Links";
    }
}
