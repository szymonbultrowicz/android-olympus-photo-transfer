#!/bin/bash
photos_dir=DCIM/100OLYMP
files=(
  "http://www.rawsamples.ch/raws/olympus/e1/RAW_OLYMPUS_E1.ORF"
  "http://www.rawsamples.ch/raws/olympus/e3/RAW_OLYMPUS_E3.ORF"
  "http://www.rawsamples.ch/raws/olympus/RAW_OLYMPUS_E5.ORF"
  "http://www.rawsamples.ch/raws/olympus/e3/RAW_OLYMPUS_E3.ORF"
  "http://www.rawsamples.ch/raws/olympus/e20/RAW_OLYMPUS_E20.ORF"
  "http://www.rawsamples.ch/raws/olympus/RAW_OLYMPUS_E30.ORF"
  "http://www.rawsamples.ch/raws/olympus/RAW_OLYMPUS_EM1.ORF"
  "http://www.rawsamples.ch/raws/olympus/RAW_OLYMPUS_EM5.ORF"
  "http://www.rawsamples.ch/raws/olympus/RAW_OLYMPUS_E-M10.ORF"
  "http://www.rawsamples.ch/raws/olympus/RAW_OLYMPUS_E-M10MARKII.ORF"
  "http://www.rawsamples.ch/raws/olympus/RAW_OLYMPUS_C5060WZ.ORF"
  "http://www.rawsamples.ch/raws/olympus/c5050z/RAW_OLYMPUS_C5050Z.ORF"
  "http://www.rawsamples.ch/raws/olympus/c8080/RAW_OLYMPUS_C8080.ORF"
  "http://www.rawsamples.ch/raws/olympus/e300/RAW_OLYMPUS_E300.ORF"
  "http://www.rawsamples.ch/raws/olympus/RAW_OLYMPUS_E-330.ORF"
  "http://www.rawsamples.ch/raws/olympus/e300/RAW_OLYMPUS_E300.ORF"
  "http://www.rawsamples.ch/raws/olympus/e410/RAW_OLYMPUS_E410.ORF"
  "http://www.rawsamples.ch/raws/olympus/RAW_OLYMPUS_E420.ORF"
  "http://www.rawsamples.ch/raws/olympus/e410/RAW_OLYMPUS_E410.ORF"
  "http://www.rawsamples.ch/raws/olympus/RAW_OLYMPUS_E-450.ORF"
  "http://www.rawsamples.ch/raws/olympus/e410/RAW_OLYMPUS_E410.ORF"
  "http://www.rawsamples.ch/raws/olympus/e500/RAW_OLYMPUS_E500.ORF"
  "http://www.rawsamples.ch/raws/olympus/e510/RAW_OLYMPUS_E510.ORF"
  "http://www.rawsamples.ch/raws/olympus/RAW_OLYMPUS_E520.ORF"
  "http://www.rawsamples.ch/raws/olympus/RAW_OLYMPUS_E600.ORF"
  "http://www.rawsamples.ch/raws/olympus/sp350/RAW_OLYMPUS_SP350.ORF"
  "http://www.rawsamples.ch/raws/olympus/sp500uz/RAW_OLYMPUS_SP500UZ.ORF"
  "http://www.rawsamples.ch/raws/olympus/RAW_OLYMPUS_EP1.ORF"
  "http://www.rawsamples.ch/raws/olympus/RAW_OLYMPUS_EP2.ORF"
  "http://www.rawsamples.ch/raws/olympus/RAW_OLYMPUS_EPL5.ORF"
  "http://www.rawsamples.ch/raws/olympus/RAW_OLYMPUS_EP3.ORF"
  "http://www.rawsamples.ch/raws/olympus/RAW_OLYMPUS_EPL3.ORF"
  "http://www.rawsamples.ch/raws/olympus/RAW_OLYMPUS_EPL5.ORF"
  "http://www.rawsamples.ch/raws/olympus/RAW_OLYMPUS_E-PL6.ORF"
  "http://www.rawsamples.ch/raws/olympus/RAW_OLYMPUS_E-PL7.ORF"
  "http://www.rawsamples.ch/raws/olympus/RAW_OLYMPUS_E-PM1.ORF"
  "http://www.rawsamples.ch/raws/olympus/RAW_OLYMPUS_XZ1.ORF"
  "http://www.rawsamples.ch/raws/olympus/RAW_OLYMPUS_XZ-2.ORF"
)
i=1
for f in "${files[@]}"
do
  fpath="$photos_dir/P$i.ORF"
  if ! [ -f $fpath ]; then
	  curl --output $fpath "$f"
	fi
	((i=i+1))
done

ufraw-batch $photos_dir/*.ORF --out-type=jpeg --overwrite
for f in $photos_dir/*.jpg; do
    mv -- "$f" "${f%.jpg}.JPG"
done
