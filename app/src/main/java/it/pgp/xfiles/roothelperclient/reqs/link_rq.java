package it.pgp.xfiles.roothelperclient.reqs;

import java.io.IOException;
import java.io.OutputStream;

import it.pgp.xfiles.io.FlushingBufferedOutputStream;
import it.pgp.xfiles.roothelperclient.ControlCodes;
import it.pgp.xfiles.utils.Misc;

public class link_rq extends PairOfPaths_rq {

    boolean isHardLink;

    public link_rq(Object fx, Object fy, boolean isHardLink) {
        super(ControlCodes.ACTION_LINK, fx, fy);
        this.isHardLink = isHardLink;
    }

    @Override
    public byte getRequestByteWithFlags() {
        byte rq = requestType.getValue();
        // customize with flag bits
        rq ^= ((isHardLink?2:0) << (ControlCodes.rq_bit_length));
        return rq;
    }

    @Override
    public void write(OutputStream outputStream) throws IOException {
        try(FlushingBufferedOutputStream nbf = new FlushingBufferedOutputStream(outputStream)) {
            nbf.write(getRequestByteWithFlags());
            // write lengths and fields
            nbf.write(Misc.castUnsignedNumberToBytes(lx,2));
            nbf.write(Misc.castUnsignedNumberToBytes(ly,2));
            nbf.write(fx);
            nbf.write(fy);
        }
    }
}
