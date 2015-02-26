use strict;
use warnings;
use IPC::Open2;
use Time::HiRes;

my $start_time = Time::HiRes::time;
my $cmd = "java -cp target/reusablejvm-0.0.1-SNAPSHOT.jar ArgsSum";

foreach ( 1 .. 100 ) {
    # コマンドを入出力モードで起動する
    my $pid = open2(*READ, *WRITE, "$cmd $_");
    #print WRITE "java ArgsSum $_\n";

    # 結果判定
    my $s = <READ>;
    print "$_ -> $s";

    # close
    close(WRITE);
    close(READ);
}

printf("\n%0.3f\n",Time::HiRes::time - $start_time);

