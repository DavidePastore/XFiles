package it.pgp.xfiles.roothelperclient.reqs;

import java.io.IOException;
import java.io.OutputStream;
import java.util.BitSet;

import it.pgp.xfiles.enums.FileMode;
import it.pgp.xfiles.io.FlushingBufferedOutputStream;
import it.pgp.xfiles.roothelperclient.ControlCodes;
import it.pgp.xfiles.utils.Misc;

/**
 * Created by pgp on 06/02/17
 */

public class singleStats_rq extends SinglePath_rq {
    private BitSet flags; // file/folder/multi
    public singleStats_rq(Object pathname, FileMode fileMode) {
        super(ControlCodes.ACTION_STATS, pathname);
        this.flags = (fileMode==FileMode.FILE ?BitSet.valueOf(new long[]{1}):BitSet.valueOf(new long[]{2})); // 0,0,1 --- 0,1,0
    }

    @Override
    public byte getRequestByteWithFlags() {
        byte rq = requestType.getValue();
        // customize with flag bits
        for (int i=0;i<ControlCodes.flags_bit_length;i++) {
            rq ^= ((flags.get(i)?1:0) << (i+ControlCodes.rq_bit_length));
        }
        return rq;
    }

    @Override
    public void write(OutputStream outputStream) throws IOException {
        try(FlushingBufferedOutputStream nbf = new FlushingBufferedOutputStream(outputStream)) {
            nbf.write(getRequestByteWithFlags());
            // write len and field
            nbf.write(Misc.castUnsignedNumberToBytes(pathname_len,2));
            nbf.write(pathname);
        }
    }
}
