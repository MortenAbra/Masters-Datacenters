mkdir benchmark
cd benchmark
dd if=/dev/zero of=benchfile bs=4k count=200000 && sync; rm benchfile