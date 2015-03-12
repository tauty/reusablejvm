use strict;
use warnings;
use IPC::Open2;
use Time::HiRes;

my $start_time = Time::HiRes::time;
my $cmd = "java -jar target/reusablejvm-0.0.1-SNAPSHOT.jar";
# コマンドを入出力モードで起動する
my $pid = open2(*READ, *WRITE, $cmd);

foreach ( 1 .. 100 ) {
    # コマンドの標準入力にデータを送る
    print WRITE "java ArgsSum $_\n";

    # 結果判定
    my $status = <READ>;
    if($status =~ /^SUCCESS/) {
        # コマンドの標準出力からデータを読み出す
        print WRITE "sysout\n";
        my $s = <READ>;
        print "$_ -> $s";
        <READ>; # skip the end token.
    } else {
        # ERROR出力
        print "input:$_, result:$status";
        print WRITE "syserr\n";
        while( <READ> ) {
            last if $_ =~ /^###END###/;
            print $_;
        }
    }
}
print WRITE "exit\n";

close(WRITE);
close(READ);
printf("\n%0.3f\n",Time::HiRes::time - $start_time);

