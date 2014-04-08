# --- First database schema

# --- !Ups

--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: brands; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE brands (
    name character varying(30) NOT NULL,
    date character varying(254) NOT NULL,
    id integer NOT NULL
);


ALTER TABLE public.brands OWNER TO postgres;

--
-- Name: brands_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE brands_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.brands_id_seq OWNER TO postgres;

--
-- Name: brands_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE brands_id_seq OWNED BY brands.id;


--
-- Name: mobiles; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE mobiles (
    username character varying(1000) NOT NULL,
    "mobile_brandId" integer NOT NULL,
    "mobile_modelId" integer NOT NULL,
    imei_meid character varying(1000) NOT NULL,
    other_imei_meid character varying(1000) NOT NULL,
    purchase_date character varying(254) NOT NULL,
    contact_no character varying(1000) NOT NULL,
    email character varying(1000) NOT NULL,
    type character varying(20) NOT NULL,
    status character varying(50) NOT NULL,
    description character varying(1000) NOT NULL,
    registration_date character varying(254) NOT NULL,
    document character varying(1000) NOT NULL,
    "otherMobileBrand" character varying(1000) NOT NULL,
    "otherMobileModel" character varying(1000) NOT NULL,
    id integer NOT NULL
);


ALTER TABLE public.mobiles OWNER TO postgres;

--
-- Name: mobiles_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE mobiles_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.mobiles_id_seq OWNER TO postgres;

--
-- Name: mobiles_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE mobiles_id_seq OWNED BY mobiles.id;


--
-- Name: mobilesmodel; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE mobilesmodel (
    model character varying(30) NOT NULL,
    mobilesnameid integer NOT NULL,
    id integer NOT NULL
);


ALTER TABLE public.mobilesmodel OWNER TO postgres;

--
-- Name: mobilesmodel_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE mobilesmodel_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.mobilesmodel_id_seq OWNER TO postgres;

--
-- Name: mobilesmodel_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE mobilesmodel_id_seq OWNED BY mobilesmodel.id;


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY brands ALTER COLUMN id SET DEFAULT nextval('brands_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY mobiles ALTER COLUMN id SET DEFAULT nextval('mobiles_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY mobilesmodel ALTER COLUMN id SET DEFAULT nextval('mobilesmodel_id_seq'::regclass);

--
-- Name: brands_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY brands
    ADD CONSTRAINT brands_pkey PRIMARY KEY (id);


--
-- Name: mobiles_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY mobiles
    ADD CONSTRAINT mobiles_pkey PRIMARY KEY (id);


--
-- Name: mobilesmodel_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY mobilesmodel
    ADD CONSTRAINT mobilesmodel_pkey PRIMARY KEY (id);


--
-- Name: mobilemodal_brand_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY mobilesmodel
    ADD CONSTRAINT mobilemodal_brand_fkey FOREIGN KEY (mobilesnameid) REFERENCES brands(id);


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--
