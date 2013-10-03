package org.bouncycastle.math.ec;

import org.bouncycastle.util.Arrays;

import java.math.BigInteger;

class LongArray
{
    /*
     * This expands 8 bit indices into 16 bit contents, by inserting 0s between bits.
     * In a binary field, this operation is the same as squaring an 8 bit number.
     */
    private static final int[] EXPANSION_TABLE = new int[] { 0x0000, 0x0001, 0x0004, 0x0005, 0x0010, 0x0011, 0x0014,
        0x0015, 0x0040, 0x0041, 0x0044, 0x0045, 0x0050, 0x0051, 0x0054, 0x0055, 0x0100, 0x0101, 0x0104, 0x0105, 0x0110,
        0x0111, 0x0114, 0x0115, 0x0140, 0x0141, 0x0144, 0x0145, 0x0150, 0x0151, 0x0154, 0x0155, 0x0400, 0x0401, 0x0404,
        0x0405, 0x0410, 0x0411, 0x0414, 0x0415, 0x0440, 0x0441, 0x0444, 0x0445, 0x0450, 0x0451, 0x0454, 0x0455, 0x0500,
        0x0501, 0x0504, 0x0505, 0x0510, 0x0511, 0x0514, 0x0515, 0x0540, 0x0541, 0x0544, 0x0545, 0x0550, 0x0551, 0x0554,
        0x0555, 0x1000, 0x1001, 0x1004, 0x1005, 0x1010, 0x1011, 0x1014, 0x1015, 0x1040, 0x1041, 0x1044, 0x1045, 0x1050,
        0x1051, 0x1054, 0x1055, 0x1100, 0x1101, 0x1104, 0x1105, 0x1110, 0x1111, 0x1114, 0x1115, 0x1140, 0x1141, 0x1144,
        0x1145, 0x1150, 0x1151, 0x1154, 0x1155, 0x1400, 0x1401, 0x1404, 0x1405, 0x1410, 0x1411, 0x1414, 0x1415, 0x1440,
        0x1441, 0x1444, 0x1445, 0x1450, 0x1451, 0x1454, 0x1455, 0x1500, 0x1501, 0x1504, 0x1505, 0x1510, 0x1511, 0x1514,
        0x1515, 0x1540, 0x1541, 0x1544, 0x1545, 0x1550, 0x1551, 0x1554, 0x1555, 0x4000, 0x4001, 0x4004, 0x4005, 0x4010,
        0x4011, 0x4014, 0x4015, 0x4040, 0x4041, 0x4044, 0x4045, 0x4050, 0x4051, 0x4054, 0x4055, 0x4100, 0x4101, 0x4104,
        0x4105, 0x4110, 0x4111, 0x4114, 0x4115, 0x4140, 0x4141, 0x4144, 0x4145, 0x4150, 0x4151, 0x4154, 0x4155, 0x4400,
        0x4401, 0x4404, 0x4405, 0x4410, 0x4411, 0x4414, 0x4415, 0x4440, 0x4441, 0x4444, 0x4445, 0x4450, 0x4451, 0x4454,
        0x4455, 0x4500, 0x4501, 0x4504, 0x4505, 0x4510, 0x4511, 0x4514, 0x4515, 0x4540, 0x4541, 0x4544, 0x4545, 0x4550,
        0x4551, 0x4554, 0x4555, 0x5000, 0x5001, 0x5004, 0x5005, 0x5010, 0x5011, 0x5014, 0x5015, 0x5040, 0x5041, 0x5044,
        0x5045, 0x5050, 0x5051, 0x5054, 0x5055, 0x5100, 0x5101, 0x5104, 0x5105, 0x5110, 0x5111, 0x5114, 0x5115, 0x5140,
        0x5141, 0x5144, 0x5145, 0x5150, 0x5151, 0x5154, 0x5155, 0x5400, 0x5401, 0x5404, 0x5405, 0x5410, 0x5411, 0x5414,
        0x5415, 0x5440, 0x5441, 0x5444, 0x5445, 0x5450, 0x5451, 0x5454, 0x5455, 0x5500, 0x5501, 0x5504, 0x5505, 0x5510,
        0x5511, 0x5514, 0x5515, 0x5540, 0x5541, 0x5544, 0x5545, 0x5550, 0x5551, 0x5554, 0x5555 };

    // For toString(); must have length 64
    private static final String ZEROES = "0000000000000000000000000000000000000000000000000000000000000000";

    private final static byte[] bitLengths =
    {
        0, 1, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4,
        5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,
        6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6,
        6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6,
        7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
        7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
        7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
        7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
        8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8,
        8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8,
        8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8,
        8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8,
        8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8,
        8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8,
        8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8,
        8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8
    };

    public static int getWordLength(int bits)
    {
        return (bits + 63) >>> 6;
    }

    // TODO make m fixed for the LongArray, and hence compute T once and for all

    private long[] m_ints;

    public LongArray(int intLen)
    {
        m_ints = new long[intLen];
    }

    public LongArray(long[] ints)
    {
        m_ints = ints;
    }

    public LongArray(BigInteger bigInt)
    {
        if (bigInt == null || bigInt.signum() < 0)
        {
            throw new IllegalArgumentException("invalid F2m field value");
        }

        if (bigInt.signum() == 0)
        {
            m_ints = new long[] { 0L };
            return;
        }

        byte[] barr = bigInt.toByteArray();
        int barrLen = barr.length;
        int barrStart = 0;
        if (barr[0] == 0)
        {
            // First byte is 0 to enforce highest (=sign) bit is zero.
            // In this case ignore barr[0].
            barrLen--;
            barrStart = 1;
        }
        int intLen = (barrLen + 7) / 8;
        m_ints = new long[intLen];

        int iarrJ = intLen - 1;
        int rem = barrLen % 8 + barrStart;
        long temp = 0;
        int barrI = barrStart;
        if (barrStart < rem)
        {
            for (; barrI < rem; barrI++)
            {
                temp <<= 8;
                int barrBarrI = barr[barrI] & 0xFF;
                temp |= barrBarrI;
            }
            m_ints[iarrJ--] = temp;
        }

        for (; iarrJ >= 0; iarrJ--)
        {
            temp = 0;
            for (int i = 0; i < 8; i++)
            {
                temp <<= 8;
                int barrBarrI = barr[barrI++] & 0xFF;
                temp |= barrBarrI;
            }
            m_ints[iarrJ] = temp;
        }
    }

    public boolean isZero()
    {
        long[] a = m_ints;
        for (int i = 0; i < a.length; ++i)
        {
            if (a[i] != 0L)
            {
                return false;
            }
        }
        return true;
    }

    public int getUsedLength()
    {
        return getUsedLengthFrom(m_ints.length);
    }

    public int getUsedLengthFrom(int from)
    {
        long[] a = m_ints;
        from = Math.min(from, a.length);

        if (from < 1)
        {
            return 0;
        }

        // Check if first element will act as sentinel
        if (a[0] != 0)
        {
            while (a[--from] == 0)
            {
            }
            return from + 1;
        }

        do
        {
            if (a[--from] != 0)
            {
                return from + 1;
            }
        }
        while (from > 0);

        return 0;
    }

    public int degree()
    {
        int i = m_ints.length;
        long w;
        do
        {
            if (i == 0)
            {
                return 0;
            }
            w = m_ints[--i];
        }
        while (w == 0);

        return (i << 6) + bitLength(w);
    }

    private static int bitLength(long w)
    {
        int u = (int)(w >>> 32), b;
        if (u == 0)
        {
            u = (int)w;
            b = 0;
        }
        else
        {
            b = 32;
        }

        int t = u >>> 16, k;
        if (t == 0)
        {
            t = u >>> 8;
            k = (t == 0) ? bitLengths[u] : 8 + bitLengths[t];
        }
        else
        {
            int v = t >>> 8;
            k = (v == 0) ? 16 + bitLengths[t] : 24 + bitLengths[v];
        }

        return b + k;
    }

    private long[] resizedInts(int newLen)
    {
        long[] newInts = new long[newLen];
        System.arraycopy(m_ints, 0, newInts, 0, Math.min(m_ints.length, newLen));
        return newInts;
    }

    public BigInteger toBigInteger()
    {
        int usedLen = getUsedLength();
        if (usedLen == 0)
        {
            return ECConstants.ZERO;
        }

        long highestInt = m_ints[usedLen - 1];
        byte[] temp = new byte[8];
        int barrI = 0;
        boolean trailingZeroBytesDone = false;
        for (int j = 7; j >= 0; j--)
        {
            byte thisByte = (byte)(highestInt >>> (8 * j));
            if (trailingZeroBytesDone || (thisByte != 0))
            {
                trailingZeroBytesDone = true;
                temp[barrI++] = thisByte;
            }
        }

        int barrLen = 8 * (usedLen - 1) + barrI;
        byte[] barr = new byte[barrLen];
        for (int j = 0; j < barrI; j++)
        {
            barr[j] = temp[j];
        }
        // Highest value int is done now

        for (int iarrJ = usedLen - 2; iarrJ >= 0; iarrJ--)
        {
            long mi = m_ints[iarrJ];
            for (int j = 7; j >= 0; j--)
            {
                barr[barrI++] = (byte)(mi >>> (8 * j));
            }
        }
        return new BigInteger(1, barr);
    }

    private static long shiftLeftQuick(long[] x)
    {
        long prev = 0;
        for (int i = 0; i < x.length; ++i)
        {
            long next = x[i];
            x[i] = (next << 1) | prev;
            prev = next >>> 63;
        }
        return prev;
    }

    public void addOneShifted(int shift)
    {
        if (shift >= m_ints.length)
        {
            m_ints = resizedInts(shift + 1);
        }

        m_ints[shift] ^= 1;
    }

    public void addShiftedByBits(LongArray other, int bits)
    {
        int words = bits >>> 6;
        int shift = bits & 0x3F;

        if (shift == 0)
        {
            addShiftedByWords(other, words);
            return;
        }

        int otherUsedLen = other.getUsedLength();
        if (otherUsedLen == 0)
        {
            return;
        }

        int minLen = otherUsedLen + words + 1;
        if (minLen > m_ints.length)
        {
            m_ints = resizedInts(minLen);
        }

        int shiftInv = 64 - shift;
        long prev = 0;
        for (int i = 0; i < otherUsedLen; ++i)
        {
            long next = other.m_ints[i];
            m_ints[i + words] ^= (next << shift) | prev;
            prev = next >>> shiftInv;
        }
        m_ints[otherUsedLen + words] ^= prev;
    }

    private static long addShiftedByBitsQuick(long[] x, long[] y, int count, int shift)
    {
        int shiftInv = 64 - shift;
        long prev = 0;
        for (int i = 0; i < count; ++i)
        {
            long next = y[i];
            x[i] ^= (next << shift) | prev;
            prev = next >>> shiftInv;
        }
        return prev;
    }

    public void addShiftedByWords(LongArray other, int words)
    {
        int otherUsedLen = other.getUsedLength();
        if (otherUsedLen == 0)
        {
            return;
        }

        int minLen = otherUsedLen + words;
        if (minLen > m_ints.length)
        {
            m_ints = resizedInts(minLen);
        }

        for (int i = 0; i < otherUsedLen; i++)
        {
            m_ints[words + i] ^= other.m_ints[i];
        }
    }

    private static void addShiftedByWordsQuick(long[] x, int xOff, long[] y)
    {
        for (int i = 0; i < y.length; ++i)
        {
            x[xOff + i] ^= y[i];
        }
    }

    private static void addQuick(long[] x, long[] y, int count)
    {
        for (int i = 0; i < count; ++i)
        {
            x[i] ^= y[i];
        }
    }

    public int getLength()
    {
        return m_ints.length;
    }

    public void flipWord(int bit, long word)
    {
        int len = m_ints.length;
        int n = bit >>> 6;
        if (n < len)
        {
            int shift = bit & 0x3F;
            if (shift == 0)
            {
                m_ints[n] ^= word;
            }
            else
            {
                m_ints[n] ^= word << shift;
                if (++n < len)
                {
                    m_ints[n] ^= word >>> (64 - shift);
                }
            }
        }
    }

    public long getWord(int bit)
    {
        int len = m_ints.length;
        int n = bit >>> 6;
        if (n >= len)
        {
            return 0;
        }
        int shift = bit & 0x3F;
        if (shift == 0)
        {
            return m_ints[n];
        }
        long result = m_ints[n] >>> shift;
        if (++n < len)
        {
            result |= m_ints[n] << (64 - shift);
        }
        return result;
    }

    public boolean testBitZero()
    {
        return m_ints.length > 0 && (m_ints[0] & 1L) != 0;
    }

    public boolean testBit(int n)
    {
        // theInt = n / 64
        int theInt = n >>> 6;
        // theBit = n % 64
        int theBit = n & 0x3F;
        long tester = 1L << theBit;
        return (m_ints[theInt] & tester) != 0;
    }

    public void flipBit(int n)
    {
        // theInt = n / 64
        int theInt = n >>> 6;
        // theBit = n % 64
        int theBit = n & 0x3F;
        long flipper = 1L << theBit;
        m_ints[theInt] ^= flipper;
    }

    public void setBit(int n)
    {
        // theInt = n / 64
        int theInt = n >>> 6;
        // theBit = n % 64
        int theBit = n & 0x3F;
        long setter = 1L << theBit;
        m_ints[theInt] |= setter;
    }

    public void clearBit(int n)
    {
        // theInt = n / 64
        int theInt = n >>> 6;
        // theBit = n % 64
        int theBit = n & 0x3F;
        long setter = 1L << theBit;
        m_ints[theInt] &= ~setter;
    }

    public LongArray multiply(LongArray other, int m)
    {
        int aLen = getUsedLength();
        if (aLen == 0)
        {
            return new LongArray(1);
        }

        int bLen = other.getUsedLength();
        if (bLen == 0)
        {
            return new LongArray(1);
        }

        LongArray A = this, B = other;
        if (aLen > bLen)
        {
            A = other; B = this;
            int tmp = aLen; aLen = bLen; bLen = tmp;
        }

        if (aLen == 1)
        {
            long aVal = A.m_ints[0];
            long[] b = B.m_ints;
            long[] c = new long[aLen + bLen];
            if ((aVal & 1L) != 0)
            {
                addQuick(c, b, bLen);
            }
            int k = 1;
            while ((aVal >>>= 1) != 0)
            {
                if ((aVal & 1L) != 0)
                {
                    addShiftedByBitsQuick(c, b, bLen, k);
                }
                ++k;
            }
            return new LongArray(c);
        }

        if ((B.m_ints[bLen - 1] >>> 49) != 0L)
        {
            ++bLen;
        }

        int cLen = aLen + bLen;

        long[] a = A.m_ints;
        long[] b = B.resizedInts(bLen);
        long[] c0 = new long[cLen];
        long[] c1 = new long[cLen];
        long[] c2 = new long[cLen];
        long[] c3 = new long[cLen];

        long bit1 = 1L << 48;

        for (;;)
        {
            long bit2 = bit1 >>> 16, bit3 = bit2 >>> 16, bit4 = bit3 >>> 16;

            for (int aPos = 0; aPos < aLen; ++aPos)
            {
                long aVal = a[aPos];
                if ((aVal & bit1) != 0)
                {
                    addShiftedByWordsQuick(c3, aPos, b);
                }
                if ((aVal & bit2) != 0)
                {
                    addShiftedByWordsQuick(c2, aPos, b);
                }
                if ((aVal & bit3) != 0)
                {
                    addShiftedByWordsQuick(c1, aPos, b);
                }
                if ((aVal & bit4) != 0)
                {
                    addShiftedByWordsQuick(c0, aPos, b);
                }
            }

            if ((bit1 <<= 1) == 0L)
            {
                break;
            }

            shiftLeftQuick(b);
        }

        addShiftedByBitsQuick(c0, c1, cLen, 16);
        addShiftedByBitsQuick(c0, c2, cLen, 32);
        addShiftedByBitsQuick(c0, c3, cLen, 48);

        return new LongArray(c0);
    }

    // public IntArray multiplyLeftToRight(IntArray other, int m) {
    // // Lenght of c is 2m bits rounded up to the next int (32 bit)
    // int t = (m + 31) / 32;
    // if (m_ints.length < t) {
    // m_ints = resizedInts(t);
    // }
    //
    // IntArray b = new IntArray(other.resizedInts(other.getLength() + 1));
    // IntArray c = new IntArray((m + m + 31) / 32);
    // // IntArray c = new IntArray(t + t);
    // int testBit = 1 << 31;
    // for (int k = 31; k >= 0; k--) {
    // for (int j = 0; j < t; j++) {
    // if ((m_ints[j] & testBit) != 0) {
    // // The kth bit of m_ints[j] is set
    // c.addShifted(b, j);
    // }
    // }
    // testBit >>>= 1;
    // if (k > 0) {
    // c.shiftLeft();
    // }
    // }
    // return c;
    // }

    public void reduce(int m, int[] ks)
    {
        int len = getUsedLength();
        int mLen = (m + 63) >>> 6;
        if (len < mLen)
        {
            return;
        }

        int _2m = m << 1;
        int pos = Math.min(_2m - 2, (len << 6) - 1);

        int kMax = ks[ks.length - 1];
        if (kMax < m - 63)
        {
            reduceWordWise(pos, m, ks);
        }
        else
        {
            reduceBitWise(pos, m, ks);
        }

        // Instead of flipping the high bits in the loop, explicitly clear any partial word above m bits
        int partial = m & 0x3F;
        if (partial != 0)
        {
            m_ints[mLen - 1] &= (1L << partial) - 1;
        }

        if (len > mLen)
        {
            m_ints = resizedInts(mLen);
        }
    }

    private void reduceBitWise(int from, int m, int[] ks)
    {
        for (int i = from; i >= m; --i)
        {
            if (testBit(i))
            {
//                clearBit(i);
                int bit = i - m;
                flipBit(bit);
                int j = ks.length;
                while (--j >= 0)
                {
                    flipBit(ks[j] + bit);
                }
            }
        }
    }

    private void reduceWordWise(int from, int m, int[] ks)
    {
        int pos = m + ((from - m) & ~0x3F);
        for (int i = pos; i >= m; i -= 64)
        {
            long word = getWord(i);
            if (word != 0)
            {
//                flipWord(i);
                int bit = i - m;
                flipWord(bit, word);
                int j = ks.length;
                while (--j >= 0)
                {
                    flipWord(ks[j] + bit, word);
                }
            }
        }
    }

    public LongArray square(int m)
    {
        int len = getUsedLength();
        if (len == 0)
        {
            return this;
        }

        int _2len = len << 1;
        long[] r = new long[_2len];

        int pos = 0;
        while (pos < _2len)
        {
            long mi = m_ints[pos >>> 1];

            {
                int mi00 = (int)mi;
                int r00 = square16(mi00 & 0xFFFF);
                int r32 = square16(mi00 >>> 16);
                r[pos++] = (r32 & 0xFFFFFFFFL) << 32 | (r00 & 0xFFFFFFFFL);
            }

            {
                int mi32 = (int)(mi >>> 32);
                int r00 = square16(mi32 & 0xFFFF);
                int r32 = square16(mi32 >>> 16);
                r[pos++] = (r32 & 0xFFFFFFFFL) << 32 | (r00 & 0xFFFFFFFFL);
            }
        }

        return new LongArray(r);
    }

    private static int square16(int n)
    {
        return EXPANSION_TABLE[n & 0xFF] | EXPANSION_TABLE[n >>> 8] << 16;
    }

    public LongArray modInverse(int m, int[] ks)
    {
        // Inversion in F2m using the extended Euclidean algorithm
        // Input: A nonzero polynomial a(z) of degree at most m-1
        // Output: a(z)^(-1) mod f(z)

        int uzDegree = degree();
        if (uzDegree == 1)
        {
            return this;
        }

        // u(z) := a(z)
        LongArray uz = (LongArray)clone();

        int t = getWordLength(m);

        // v(z) := f(z)
        LongArray vz = new LongArray(t);
        vz.setBit(m);
        vz.setBit(0);
        vz.setBit(ks[0]);
        if (ks.length > 1) 
        {
            vz.setBit(ks[1]);
            vz.setBit(ks[2]);
        }

        // g1(z) := 1, g2(z) := 0
        LongArray g1z = new LongArray(t);
        g1z.setBit(0);
        LongArray g2z = new LongArray(t);

        while (uzDegree != 0)
        {
            // j := deg(u(z)) - deg(v(z))
            int j = uzDegree - vz.degree();

            // If j < 0 then: u(z) <-> v(z), g1(z) <-> g2(z), j := -j
            if (j < 0) 
            {
                final LongArray uzCopy = uz;
                uz = vz;
                vz = uzCopy;

                final LongArray g1zCopy = g1z;
                g1z = g2z;
                g2z = g1zCopy;

                j = -j;
            }

            // u(z) := u(z) + z^j * v(z)
            // Note, that no reduction modulo f(z) is required, because
            // deg(u(z) + z^j * v(z)) <= max(deg(u(z)), j + deg(v(z)))
            // = max(deg(u(z)), deg(u(z)) - deg(v(z)) + deg(v(z))
            // = deg(u(z))
            // uz = uz.xor(vz.shiftLeft(j));
            uz.addShiftedByBits(vz, j);
            uzDegree = uz.degree();

            // g1(z) := g1(z) + z^j * g2(z)
//            g1z = g1z.xor(g2z.shiftLeft(j));
            if (uzDegree != 0)
            {
                g1z.addShiftedByBits(g2z, j);
            }
        }
        return g2z;
    }

    public boolean equals(Object o)
    {
        if (!(o instanceof LongArray))
        {
            return false;
        }
        LongArray other = (LongArray) o;
        int usedLen = getUsedLength();
        if (other.getUsedLength() != usedLen)
        {
            return false;
        }
        for (int i = 0; i < usedLen; i++)
        {
            if (m_ints[i] != other.m_ints[i])
            {
                return false;
            }
        }
        return true;
    }

    public int hashCode()
    {
        int usedLen = getUsedLength();
        int hash = 1;
        for (int i = 0; i < usedLen; i++)
        {
            long mi = m_ints[i];
            hash *= 31;
            hash ^= (int)mi;
            hash *= 31;
            hash ^= (int)(mi >>> 32);
        }
        return hash;
    }

    public Object clone()
    {
        return new LongArray(Arrays.clone(m_ints));
    }

    public String toString()
    {
        int i = getUsedLength();
        if (i == 0)
        {
            return "0";
        }

        StringBuffer sb = new StringBuffer(Long.toBinaryString(m_ints[--i]));
        while (--i >= 0)
        {
            String s = Long.toBinaryString(m_ints[i]);

            // Add leading zeroes, except for highest significant word
            int len = s.length();
            if (len < 64)
            {
                sb.append(ZEROES.substring(len));
            }

            sb.append(s);
        }
        return sb.toString();
    }
}