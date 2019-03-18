package com.share.lifetime.common.util;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SftpUtils {

    public static boolean download(String host, int port, String username, String password, String src, String dst) {
        boolean flag = true;
        Session session = null;
        Channel channel = null;
        ChannelSftp channelSftp = null;
        try {
            session = JschUtils.getSession(host, port, username, password);
            session.connect();
            channel = session.openChannel(JschUtils.SFTP_TYPE);
            channel.connect();
            channelSftp = (ChannelSftp)channel;
            channelSftp.setFilenameEncoding("UTF-8");
            channelSftp.get(src, dst);
            channelSftp.quit();
        } catch (JSchException e) {
            flag = false;
            log.error(ExceptionUtils.getStackTrace(e));
        } catch (SftpException e) {
            flag = false;
            log.error(ExceptionUtils.getStackTrace(e));
        } finally {
            disconnect(session, channel, channelSftp);
            if (!flag) {
                log.warn("host:{},port:{},username:{},src:{},dst:{}", host, port, username, src, dst);
            }
        }
        return flag;
    }

    public static boolean upload(String host, int port, String username, String password, String src, String dst) {
        boolean flag = true;
        Session session = null;
        Channel channel = null;
        ChannelSftp channelSftp = null;
        try {
            session = JschUtils.getSession(host, port, username, password);
            session.connect();
            channel = session.openChannel(JschUtils.SFTP_TYPE);
            channel.connect();
            channelSftp = (ChannelSftp)channel;
            channelSftp.put(src, dst);
            channelSftp.quit();
        } catch (JSchException e) {
            flag = false;
            log.error(ExceptionUtils.getStackTrace(e));
        } catch (SftpException e) {
            flag = false;
            log.error(ExceptionUtils.getStackTrace(e));
        } finally {
            disconnect(session, channel, channelSftp);
            if (!flag) {
                log.warn("host:{},port:{},username:{},src:{},dst:{}", host, port, username, src, dst);
            }
        }
        return flag;
    }

    @SuppressWarnings("rawtypes")
    public static int getFileNumber(String host, int port, String username, String password, String path, String prefix,
        String suffix) {
        int number = 0;
        Session session = null;
        Channel channel = null;
        ChannelSftp channelSftp = null;
        try {
            session = JschUtils.getSession(host, port, username, password);
            session.connect();
            channel = session.openChannel(JschUtils.SFTP_TYPE);
            channel.connect();
            channelSftp = (ChannelSftp)channel;
            channelSftp.setFilenameEncoding("UTF-8");
            java.util.Vector vv = channelSftp.ls(path);
            if (vv != null) {
                String filename = "";
                for (int i = 0; i < vv.size(); i++) {
                    Object obj = vv.elementAt(i);
                    if (obj instanceof com.jcraft.jsch.ChannelSftp.LsEntry) {
                        filename = ((com.jcraft.jsch.ChannelSftp.LsEntry)obj).getFilename();
                        // String longname = ((com.jcraft.jsch.ChannelSftp.LsEntry)obj).getLongname();
                        if (filename.startsWith(prefix) && filename.endsWith(suffix)) {
                            number++;
                        }
                    }
                }
            }
            channelSftp.quit();
            log.debug("ls:{}", vv);
        } catch (JSchException e) {
            number = -1;
            log.error(ExceptionUtils.getStackTrace(e));
        } catch (SftpException e) {
            number = -1;
            log.error(ExceptionUtils.getStackTrace(e));
        } finally {
            disconnect(session, channel, channelSftp);
            if (number == -1) {
                log.warn("host:{},port:{},username:{},path:{}", host, port, username, path);
            }
        }
        return number;
    }

    private static void disconnect(Session session, Channel channel, ChannelSftp channelSftp) {
        if (channelSftp != null) {
            channelSftp.disconnect();
        }
        if (channel != null) {
            channel.disconnect();
        }
        if (session != null) {
            session.disconnect();
        }
    }

}
