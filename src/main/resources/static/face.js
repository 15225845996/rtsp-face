
let faceCascade = null;
let gray = null;

let outputCanvas = document.getElementById("outputCanvas");

//缩放系数
let ratio = 3;

//获取图片
function getFaceImg() {
    var source = document.getElementById("source")
    this.img=source.toDataURL('image/png')

    //获取完整的base64编码
    this.img=img.split(',')[1];
    return this.img;
}


function getFaces(data) {
    var result = [];
    // Capture a frame
    let src = cv.matFromImageData(data);
    var width = data.width;
    var height = data.height;
    if(gray == null){
        gray = new cv.Mat(width, height, cv.CV_8UC1);
    }
    // Convert to greyscale
    cv.cvtColor(src, gray, cv.COLOR_RGBA2GRAY);

    // Downsample
    let downSampled = new cv.Mat(height/ratio, width/ratio, cv.CV_8UC1);
    /*cv.pyrDown(gray, downSampled);
    cv.pyrDown(downSampled, downSampled);*/

    cv.resize(gray, downSampled, downSampled.size());

    // Detect faces
    let faces = new cv.RectVector();
    var detectMultiScale = faceCascade.detectMultiScale;
    faceCascade.detectMultiScale(downSampled, faces)
    for (let i = 0; i < faces.size(); ++i) {
        let face = faces.get(i);
        var point = {};
        var x = face.x * ratio
        var y =  face.y * ratio;

        var w = (face.x + face.width) * ratio - x;
        var h = (face.y + face.height) * ratio - y;
        point['x'] = x;
        point['y'] = y;
        point['w'] = w;
        point['h'] = h;
        result.push(point)
    }

    // Show image
    cv.imshow(outputCanvas, downSampled)

    // Free memory
    downSampled.delete();
    faces.delete();
    src.delete();

    if(result.length > 0){
        console.log("faces.length="+result.length);
    }

    return result;
}


function getFaces2(data) {
    var result = [];
    // Capture a frame
    var height1 = 200
    var source = document.getElementById("source");
    let ctx = source.getContext("2d");
    let imgData = ctx.getImageData(0, source.height - height1, source.width, height1);
    let src = cv.matFromImageData(imgData);

    var width = data.width;
    var height = data.height;
    if(gray == null){
        gray = new cv.Mat(width, height, cv.CV_8UC1);
    }
    // Convert to greyscale
    cv.cvtColor(src, gray, cv.COLOR_RGBA2GRAY);

    // Downsample
    /*let downSampled = gray;*/
    let downSampled = new cv.Mat(height1/ratio, width/ratio, cv.CV_8UC1);
    /*cv.pyrDown(gray, downSampled);
    cv.pyrDown(downSampled, downSampled);*/

    cv.resize(gray, downSampled, downSampled.size());

    // Detect faces
    let faces = new cv.RectVector();
    var detectMultiScale = faceCascade.detectMultiScale;
    faceCascade.detectMultiScale(downSampled, faces)
    for (let i = 0; i < faces.size(); ++i) {
        let face = faces.get(i);
        var point = {};
        var x = face.x * ratio
        var y =  face.y * ratio;

        var w = (face.x + face.width) * ratio - x;
        var h = (face.y + face.height) * ratio - y;
        point['x'] = x;
        point['y'] = y+ (height - height1);
        point['w'] = w;
        point['h'] = h;
        result.push(point)
    }

    // Show image
    cv.imshow(outputCanvas, downSampled)

    // Free memory
    downSampled.delete();
    faces.delete();
    src.delete();

    if(result.length > 0){
        console.log("faces.length="+result.length);
    }

    return result;
}


//TODO 有问题，还不能用
function compareFace(source,target) {
    let sourceCtx = source.getContext("2d");
    let sourceData = sourceCtx.getImageData(0, 0, source.width, source.height);
    let sourceMat = cv.matFromImageData(sourceData);

    let targetCtx = target.getContext("2d");
    let targetData = targetCtx.getImageData(0, 0, target.width, target.height);
    let targetMat = cv.matFromImageData(targetData);

    //颜色范围
    let ranges = [0,255];
    //直方图大小， 越大匹配越精确 (越慢)
    let histSize = 1000;

    let sourceMat1 = new cv.Mat();
    let targetMat1 = new cv.Mat();

    cv.calcHist(sourceMat.data, 0, new cv.Mat(), sourceMat1, histSize, ranges);
    cv.calcHist(targetMat.data, 0, new cv.Mat(), targetMat1, histSize, ranges);

    var res = cv.compareHist(sourceMat1, targetMat1, cv.CV_COMP_CORREL);
    return res;
}



function run() {
    faceCascade = new cv.CascadeClassifier();
    faceCascade.load("face.xml")
}


// Config OpenCV
var Module = {
    locateFile: function (name) {
        let files = {
            "opencv_js.wasm": '/opencv/opencv_js.wasm'
        }
        return files[name]
    },
    preRun: [() => {
        Module.FS_createPreloadedFile("/", "face.xml", "model/haarcascade_frontalface_alt.xml",
        true, false);
    }],
    postRun: [() => {
        faceCascade = new cv.CascadeClassifier();
        faceCascade.load("face.xml")
    }]
};