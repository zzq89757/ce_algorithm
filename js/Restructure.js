//length必须为正整数

//从40开始的插入片段投入量
function getInsertPay(length) {
    return length > 5 ? 200 : length * 40;
}

//从20开始的多个插入片段投入量
function getMuitlInsertPay(length) {
    return length > 10 ? 200 : length * 20
}
//插入片段投入量计算直接调用现成函数即可

//从50开始的载体投入量 上限为 200 （突变载体上限为400）
function getLoadPay(load, max = 200) {
    let step = 20;
    return load <= 2 ? 50 : (load > max / step ? max : 20 * load);
}

//快速克隆
function simpleClone(insert) {
    return getInsertPay(insert);
}

//单片段克隆
function singleClone(load, insert) {
    return { loadPay: getLoadPay(load), insertpay: getInsertPay(insert) };
}
console.log(singleClone(12, 12));

//多片段克隆
function multiFragment(load, ...inserts) {
    let ary = [];
    for (let arg of inserts) {
        ary.push(getMuitlInsertPay(arg));
    }
    return { loadpay: getLoadPay(load), insertpay: ary }
}

console.log(multiFragment(10, 10, 10));

//单点突变
function singlePoint(mutload) {
    return getLoadPay(mutload, 400);
}
console.log(singlePoint(12));
//双位点突变
function doublePoint(point1, point2) {
    return { point1: getMuitlInsertPay(point1), point2: getInsertPay(point2) };
}
console.log(doublePoint(1, 1));
//多点突变
//插入片段数目不确定 使用不定参数
function muitlPoint(...args) {
    let obj = {};
    for (let i = 0; i < args.length; i++) {
        let key = "insert" + (i + 1);
        obj[key] = getMuitlInsertPay(args[i])
    }
    return obj;
}
console.log(muitlPoint(1, 2, 3));