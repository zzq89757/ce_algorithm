function Tmcal(seq) {
    let length = seq.length;
    let dH = 0;
    let dS = 15.1;
    let GCNum = 0;
    for (let i = 0; i < length; i++) {
        if (seq[i] == "G" || seq[i] == "C") {
            GCNum++;
        }
        let kmer = seq.substr(i, 2);
        switch (kmer) {
            case "AA":
            case "TT":
                dH = dH + 9.1;
                dS = dS + 24;
                break;
            case "AC":
            case "GT":
                dH = dH + 6.5;
                dS = dS + 17.3;
                break;
            case "AG":
            case "CT":
                dH = dH + 7.8;
                dS = dS + 20.8;
                break;
            case "AT":
                dH = dH + 8.6;
                dS = dS + 23.;
                break;
            case "TA":
                dH = dH + 6;
                dS = dS + 16.9;
                break;
            case "TC":
            case "GA":
                dH = dH + 5.6;
                dS = dS + 13.5;
                break;
            case "TG":
            case "CA":
                dH = dH + 5.8;
                dS = dS + 12.9;
                break;
            case "CC":
            case "GG":
                dH = dH + 11;
                dS = dS + 26.6;
                break;
            case "CG":
                dH = dH + 11.9;
                dS = dS + 27.8;
                break;
            case "GC":
                dH = dH + 11.1;
                dS = dS + 26.7;
                break;
        }
    }
    dH *= -1000;
    dS *= -1;
    let tm = (dH / (dS + 1.987 * Math.log(250 / 4 * Math.pow(10, -12))) + 16.6 / Math.log(10) * Math.log(204.92 * Math.pow(10, -3) / (1 + 0.7 * 204.92 * Math.pow(10, -3))) - 273.15).toFixed(1);
    let tuihuo = tm < 72 ? tm : new Number(72).toFixed(1);
    let gcContent = (GCNum / length * 100).toFixed(2) + "%";
    return { length, tm, tuihuo, GCNum, gcContent };
}
let seq = Tmcal("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAC")
console.log(seq);