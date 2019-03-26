#!/usr/bin/perl

# This script tests the RAD-55 response of the Imaging Document Source simulator.
# It looks through all of the files stored in the image cache for a simulator
# and performs a WADO retrieve for each one. This is a test of the success path.

use strict;
use File::Find;
use LWP::UserAgent();

sub check_args {
 my $arg_count = scalar(@_);
 die "Arguments: <base URL>\n" .
     "           base URL: URL used for WADO retrieves\n"

 if ($arg_count != 1);
}

sub extract {
 my ($response) = @_;
 return ($response->code, $response->status_line, $response->header("content_type"));
}

sub documentation {
 my $case_number = shift;
 my $description = shift;

 print "Exception case number: $case_number, Description: $description\n";
 print STDERR
	"Exception case number: $case_number, Description: $description\n";

 for my $s (@_) {
  print "\t$s\n";
 }
}

sub wado_retrieve {
 my ($line, $base, $accept, $output) = @_;
 return if (substr($line, 0, 1) eq "#");

 my $ua = LWP::UserAgent->new;
 $ua->timeout(180);

 my ($study, $series, $instance, $file) = split /\//, $line;

 my $url = $base . "?requestType=WADO";
 $url .= "&contentType=$accept" if ($accept ne "");
 $url .= "&studyUID=$study"	if ($study ne "");
 $url .= "&seriesUID=$series"	if ($series ne "");
 $url .= "&objectUID=$instance"	if ($instance ne "");

 my $response = $ua->get($url, "Accept" => "$accept", ":content_file" => "$output");

 return ($response);
}

sub exception_case {
 my ($case_number, $line, $base, $accept, $output, $expectedHTTPCode) = @_;

 my ($response) = wado_retrieve($line, $base, $accept, $output);
 my ($code, $status_line, $rtn_content_type) = extract($response);
 if ($code ne $expectedHTTPCode) {
  print
	"ERROR: Case number: $case_number return HTTP code ($code) does not equal expected code ($expectedHTTPCode)\n" .
	"       $status_line\n";
  

  print STDERR
	"ERROR: Case number: $case_number return HTTP code ($code) does not equal expected code ($expectedHTTPCode)\n" .
	"       $status_line\n";
 }
}

sub case_101 {
 my ($base) = @_;
 my $line = "1.3.6.1.4.1.21367.201599.1.201604021949024/1.3.6.1.4.1.21367.201599.2.201604021949024/1.3.6.1.4.1.21367.201599.3.201604021949024.1/1.2.840.10008.1.2.4.70";
 documentation("101", "Request image/jpeg where no representation exists; expect 404 error", $line);
 exception_case("101", $line, $base, "image/jpeg", "/tmp/abc", 404);
}

sub case_102 {
 my ($base) = @_;
 my $line = "1.3.6.1.4.1.21367.201599.1.201604021949024/1.3.6.1.4.1.21367.201599.2.201604021949024//1.2.840.10008.1.2.4.70";
 documentation("102", "Omit object UID; expect 400 error", $line);
 exception_case("102", $line, $base, "application/dicom", "/tmp/abc", 400);
}

sub case_103 {
 my ($base) = @_;
 my $line = "1.3.6.1.4.1.21367.201599.1.201604021949024//1.3.6.1.4.1.21367.201599.3.201604021949024.1/1.2.840.10008.1.2.4.70";
 documentation("103", "Omit series UID; expect 400 error", $line);
 exception_case("103", $line, $base, "application/dicom", "/tmp/abc", 400);
}

sub case_104 {
 my ($base) = @_;
 my $line = "/1.3.6.1.4.1.21367.201599.2.201604021949024/1.3.6.1.4.1.21367.201599.3.201604021949024.1/1.2.840.10008.1.2.4.70";
 documentation("104", "Omit study UID; expect 400 error", $line);
 exception_case("104", $line, $base, "application/dicom", "/tmp/abc", 400);
}

check_args(@ARGV);

my ($base) = @ARGV;

my $failed_operations = 0;
print STDERR "Base URL: $base\n";

my $status = 0;
my $status_string = "";

case_101($base);
case_102($base);
case_103($base);
case_104($base);

