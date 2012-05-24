package org.xmpp;

import junit.framework.TestCase;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.jinglenodes.jingle.info.Info;
import org.jinglenodes.jingle.Jingle;
import org.jinglenodes.jingle.reason.Reason;
import org.jinglenodes.jingle.content.Content;
import org.jinglenodes.jingle.description.Description;
import org.jinglenodes.jingle.description.Payload;
import org.jinglenodes.jingle.reason.ReasonType;
import org.jinglenodes.jingle.transport.Candidate;
import org.jinglenodes.jingle.transport.RawUdpTransport;
import org.xmpp.packet.IQ;
import org.xmpp.tinder.JingleIQ;

public class TestParser extends TestCase {

    final private String source = "<jingle xmlns=\"urn:xmpp:jingle:1\" action=\"session-initiate\" sid=\"abc\" initiator=\"a@a.com\" responder=\"b@b.com\">\n" +
            "  <content creator=\"initiator\" name=\"audio\" senders=\"both\">\n" +
            "    <description xmlns=\"urn:xmpp:jingle:apps:rtp:1\" media=\"audio\"></description>\n" +
            "    <transport xmlns=\"urn:xmpp:jingle:transports:raw-udp:1\">\n" +
            "      <candidate ip=\"10.166.108.22\" port=\"10000\" generation=\"0\" type=\"host\"/>\n" +
            "    </transport>\n" +
            "  </content>\n" +
            "</jingle>";
    final private String altSource = "<jingle xmlns=\"urn:xmpp:jingle:1\" action=\"session-initiate\" sid=\"abc\" initiator=\"a@a.com\" responder=\"b@b.com\">  <content creator=\"initiator\" name=\"audio\" senders=\"both\"><description xmlns=\"urn:xmpp:jingle:apps:rtp:1\" media=\"audio\">      <payload-type id=\"18\" name=\"G729\" clockrate=\"0\" channels=\"1\"/></description><transport xmlns=\"urn:xmpp:jingle:transports:raw-udp:1\">      <candidate ip=\"10.166.108.22\" port=\"10000\" generation=\"0\" type=\"host\"/></transport></content></jingle>";
    final String initiator = "a@a.com";
    final String responder = "b@b.com";

    public void testGenParser() {
        final Jingle jingle = new Jingle("abc", initiator, responder, Jingle.Action.session_initiate);
        jingle.setContent(new Content(Content.Creator.initiator, "audio", Content.Senders.both, new Description("audio"), new RawUdpTransport(new Candidate("10.166.108.22", "10000", "0"))));
        jingle.getContent().getDescription().addPayload(Payload.G729);
        final JingleIQ jingleIQ = new JingleIQ(jingle);
        //assertEquals(jingleIQ.getChildElement().element("jingle").asXML(), source);
        System.out.println(jingleIQ.toXML());
        final JingleIQ jingleIQParsed = JingleIQ.fromXml(jingleIQ);
        final String jingleString = jingleIQParsed.getJingle().toString();
        System.out.println(jingleIQParsed.getJingle().asXML());
        assertEquals(source, jingleIQParsed.getJingle().asXML());
        assertEquals(jingleIQParsed.getJingle().getInitiator(), initiator);
        System.out.println(source);
    }

    final private String sourceTerminate = "<jingle xmlns=\"urn:xmpp:jingle:1\" action=\"session-terminate\" sid=\"abc\" initiator=\"a@a.com\" responder=\"b@b.com\">\n" +
            "<reason/>\n" +
            "</jingle>";


    public void testDoubleParse() throws DocumentException {

        final String initiator = "romeo@localhost";
        final String responder = "juliet@localhost";
        final String packet = "<jingle xmlns=\"urn:xmpp:jingle:1\" action=\"session-initiate\" initiator=\"" + initiator + "\" responder=\"" + responder + "\" sid=\"37665\"><content xmlns=\"\" creator=\"initiator\" name=\"audio\" senders=\"both\"><description xmlns=\"urn:xmpp:jingle:apps:rtp:1\"><payload-type xmlns=\"\" id=\"0\" name=\"PCMU\"/></description><transport xmlns=\"urn:xmpp:jingle:transports:raw-udp:1\"><candidate xmlns=\"\" ip=\"192.168.20.172\" port=\"22000\" generation=\"0\"/></transport></content></jingle>";

        Document doc = DocumentHelper.parseText(packet);

        final IQ iq = new IQ(doc.getRootElement());
        final JingleIQ jingleIQ = JingleIQ.fromXml(iq);
        jingleIQ.setFrom(initiator);
        jingleIQ.setTo("sip.localhost");

        final JingleIQ newJingle = JingleIQ.fromXml(jingleIQ);
        Content c = newJingle.getJingle().getContent();
        assertTrue(newJingle.getJingle().getContent().getDescription() != null);
    }

    public void testGenParserTerminate() {
        final Jingle jingle = new Jingle("abc", initiator, responder, Jingle.Action.session_terminate);
        jingle.setReason(new Reason(new ReasonType(ReasonType.Name.success)));
        final JingleIQ jingleIQ = new JingleIQ(jingle);
        assertEquals(jingleIQ.getChildElement().asXML(), sourceTerminate);
        System.out.println(jingleIQ.toXML());
        final JingleIQ jingleIQParsed = JingleIQ.fromXml(jingleIQ);
        System.out.println(jingleIQParsed.getChildElement().element("jingle").asXML());
        assertEquals(sourceTerminate, jingleIQParsed.getChildElement().element("jingle").asXML());
        assertEquals(jingleIQParsed.getJingle().getInitiator(), initiator);
    }

    public void testGenInfo() throws DocumentException{
        final String packet = "<iq from=\"juliet@capulet.lit/balcony\" id=\"hg4891f5\" to=\"romeo@montague.lit/orchard\" type=\"set\"> <jingle xmlns=\"urn:xmpp:jingle:1\" action=\"session-info\" initiator=\"romeo@montague.lit/orchard\" sid=\"a73sjjvkla37jfea\"> <mute xmlns=\"urn:xmpp:jingle:apps:rtp:info:1\" creator=\"responder\" name=\"voice\"/> </jingle> </iq>";
        Document doc = DocumentHelper.parseText(packet);

        final IQ iq = new IQ(doc.getRootElement());
        final JingleIQ jingleIQ = JingleIQ.fromXml(iq);
        System.out.println(jingleIQ);
        Info info = jingleIQ.getJingle().getInfo();
        System.out.println(info);
    }

    public void testRingingPacket(){

        final String initiator = "romeo@localhost";
        final String responder = "juliet@localhost";

        final Jingle jingle = new Jingle("12121", initiator, responder, Jingle.Action.session_info);
        jingle.setInfo(new Info(Info.Type.ringing));
        final JingleIQ iq = new JingleIQ(jingle);
        iq.setTo(initiator);
        iq.setFrom(responder);

        System.out.println(jingle.toString());
        System.out.println(iq.toXML());

    }

    final String initiateExample = "<iq type=\"set\" id=\"880BF095-217C-4723-A544-8AB154E17BA0\" to=\"sip.yuilop.tv\" from=\"+4915634567890\n" +
            "@yuilop.tv/I(1.4.0.20120515)(Xx/IHylQbJOau1uE6xiQua39scU=)\"><jingle xmlns=\"urn:xmpp:jingle:1\" action=\"session-initiate\" sid=\"65A377CF25AD46D7B5A324F063002247\" initiator=\"+4915634567890@yuilop.tv/I(1.4.0\n" +
            ".20120515)(Xx/IHylQbJOau1uE6xiQua39scU=)\" responder=\"004915738512829@sip.yuilop.tv\">\n" +
            "  <content creator=\"initiator\" name=\"voice\">\n" +
            "    <description xmlns=\"urn:xmpp:jingle:apps:rtp:1\" media=\"audio\">\n" +
            "      <payload-type id=\"0\" name=\"PCMU\" clockrate=\"8000\" channels=\"1\"/>\n" +
            "      <payload-type id=\"8\" name=\"PCMA\" clockrate=\"8000\" channels=\"1\"/>\n" +
            "      <payload-type id=\"104\" name=\"iLBC\" clockrate=\"8000\" channels=\"1\"/>\n" +
            "      <payload-type id=\"18\" name=\"G729\" clockrate=\"8000\" channels=\"1\"/>\n" +
            "      <payload-type id=\"3\" name=\"GSM\" clockrate=\"8000\" channels=\"1\"/>\n" +
            "    </description>\n" +
            "    <transport>\n" +
            "      <candidate ip=\"10.166.108.174\" port=\"4000\" type=\"host\"/>\n" +
            "    </transport>\n" +
            "  </content>\n" +
            "</jingle></iq> ";

     final String acceptExample = "<iq type=\"set\" id=\"73-62\" to=\"+4915634567890@yuilop.tv/I(1.4.0.20120515)(Xx/IHylQbJOau1uE6xiQua39scU=)\" from=\"0049\n" +
             "15738512829@sip.yuilop.tv\"><jingle xmlns=\"urn:xmpp:jingle:1\" action=\"session-accept\" sid=\"65A377CF25AD46D7B5A324F063002247\" initiator=\"+4915634567890@194.183.72.28/sip\" responder=\"004915738512829@sip.yu\n" +
             "ilop.tv\">\n" +
             "  <content creator=\"initiator\" name=\"root\" senders=\"both\">\n" +
             "    <description xmlns=\"urn:xmpp:jingle:apps:rtp:1\" media=\"audio\">\n" +
             "      <payload-type id=\"18\" name=\"G729\" clockrate=\"8000\" channels=\"1\"/>\n" +
             "      <payload-type id=\"3\" name=\"GSM\" clockrate=\"8000\" channels=\"1\"/>\n" +
             "      <payload-type id=\"8\" name=\"PCMA\" clockrate=\"8000\" channels=\"1\"/>\n" +
             "      <payload-type id=\"0\" name=\"PCMU\" clockrate=\"8000\" channels=\"1\"/>\n" +
             "    </description>\n" +
             "    <transport xmlns=\"urn:xmpp:jingle:transports:raw-udp:1\">\n" +
             "      <candidate ip=\"87.230.83.87\" port=\"6070\" generation=\"0\" type=\"host\"/>\n" +
             "    </transport>\n" +
             "  </content>\n" +
             "</jingle></iq>";

    final String terminateExample = "<iq type=\"set\" id=\"758-53\" to=\"+4915634567890@yuilop.tv/I(1.4.0.20120515)(Xx/IHylQbJOau1uE6xiQua39scU=)\" from=\"004\n" +
            "915738512829@sip.yuilop.tv/as5a1f65c0\"><jingle xmlns=\"urn:xmpp:jingle:1\" action=\"session-terminate\" sid=\"2E9C45EB2AF84F59BDB4D281060B63AF\" initiator=\"+4915634567890@194.183.72.28/sip\" responder=\"0049157\n" +
            "38512829@sip.yuilop.tv/as5a1f65c0\">\n" +
            "  <reason>\n" +
            "    <type>no_error</type>\n" +
            "  </reason>\n" +
            "</jingle></iq>";

    final String infoExample = " <iq type=\"set\" id=\"134-61\" to=\"+4915634567890@yuilop.tv/I(1.4.0.20120515)(Xx/IHylQbJOau1uE6xiQua39scU=)\" from=\"004\n" +
            "915738512829@sip.yuilop.tv/as677d099c\"><jingle xmlns=\"urn:xmpp:jingle:1\" action=\"session-info\" sid=\"65A377CF25AD46D7B5A324F063002247\" initiator=\"+4915634567890@194.183.72.28/sip\" responder=\"004915738512\n" +
            "829@sip.yuilop.tv/as677d099c\">\n" +
            "  <ringing xmlns=\"urn:xmpp:jingle:apps:rtp:info:1\"></ringing>\n" +
            "</jingle></iq>";

}
