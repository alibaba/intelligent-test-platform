

angular.module("app.diff", []).directive('diffSmoke', function () {

    var WHITE_SPACES = {' ': true, '\t': true, '\n': true, '\f': true, '\r': true};
    var fnIsJunk = function (c) {
        return WHITE_SPACES.hasOwnProperty(c)
    };
    var stripLinebreaks = function (c) {
        return c.replace(/^[\n\r]*|[\n\r]*$/g, '')
    };
    var calcRatio = function (matches, length) {
        return length ? 2.0 * matches / length : 1.0
    };

    var isInDict = function (dict) {
        return function (key) {
            return dict.hasOwnProperty(key)
        }
    };
    var safeGet = function (dict, key, defaultValue) {
        return dict.hasOwnProperty(key) ? dict[key] : defaultValue
    };

    var stringAsLines = function (str) {
        var lf = str.indexOf('\n');
        var cr = str.indexOf('\r');
        var br = str.indexOf('<br>');
        var linebreak = br > -1 ? '<br>':((lf > -1 && cr > -1) || cr < 0) ? '\n' : '\r';

        var lines = str.split(linebreak);
        for (var i = 0; i < lines.length; i++) {
            lines[i] = stripLinebreaks(lines[i])
        }

        return lines
    }
    var fnReduce = function (func, list, initial) {
        var value = null;
        var idx = 0;
        if (initial != null) {
            value = initial;
        } else if (list) {
            value = list[0];
            idx = 1;
        } else {
            return null;
        }

        for (; idx < list.length; idx++) {
            value = func(value, list[idx]);
        }

        return value;
    };
    var compareTuple = function (a, b) {
        var mlen = Math.max(a.length, b.length);
        for (var i = 0; i < mlen; i++) {
            if (a[i] < b[i]) return -1;
            if (a[i] > b[i]) return 1;
        }

        return a.length === b.length ? 0 : (a.length < b.length ? -1 : 1)
    };

    var SequenceMatcher = function (a, b, isJunk) {
        this.set_seqs = function (a, b) {
            this.set_seq1(a);
            this.set_seq2(b);
        }

        this.set_seq1 = function (a) {
            if (a === this.a) return;
            this.a = a;
            this.matching_blocks = this.opcodes = null;
        }

        this.set_seq2 = function (b) {
            if (b === this.b) return;
            this.b = b;
            this.matching_blocks = this.opcodes = null;
            this.__chain_b();
        }

        this.__chain_b = function () {
            var b = this.b;
            var n = b.length;
            var b2j = this.b2j = {};
            var populardict = {};
            var elt;
            for (var i = 0; i < b.length; i++) {
                elt = b[i];
                if (b2j.hasOwnProperty(elt)) {
                    var indices = b2j[elt];
                    if (n >= 200 && indices.length * 100 > n) {
                        populardict[elt] = 1;
                        delete b2j[elt];
                    } else {
                        indices.push(i);
                    }
                } else {
                    b2j[elt] = [i];
                }
                3
            }

            for (elt in populardict) {
                if (populardict.hasOwnProperty(elt)) {
                    delete b2j[elt];
                }
            }

            var isjunk = this.isjunk;
            var junkdict = {};
            if (isjunk) {
                for (var elt in populardict) {
                    if (populardict.hasOwnProperty(elt) && isjunk(elt)) {
                        junkdict[elt] = 1;
                        delete populardict[elt];
                    }
                }
                for (var elt in b2j) {
                    if (b2j.hasOwnProperty(elt) && isjunk(elt)) {
                        junkdict[elt] = 1;
                        delete b2j[elt];
                    }
                }
            }

            this.isbjunk = isInDict(junkdict);
        }

        this.find_longest_match = function (alo, ahi, blo, bhi) {
            var a = this.a;
            var b = this.b;
            var b2j = this.b2j;
            var isbjunk = this.isbjunk;
            var besti = alo;
            var bestj = blo;
            var bestsize = 0;
            var j = null;
            var k;

            var j2len = {};
            var nothing = [];
            for (var i = alo; i < ahi; i++) {
                var newj2len = {};
                var jdict = safeGet(b2j, a[i], nothing);
                for (var jkey in jdict) {
                    if (jdict.hasOwnProperty(jkey)) {
                        j = jdict[jkey];
                        if (j < blo) continue;
                        if (j >= bhi) break;
                        newj2len[j] = k = safeGet(j2len, j - 1, 0) + 1;
                        if (k > bestsize) {
                            besti = i - k + 1;
                            bestj = j - k + 1;
                            bestsize = k;
                        }
                    }
                }
                j2len = newj2len;
            }

            while (besti > alo && bestj > blo && !isbjunk(b[bestj - 1]) && a[besti - 1] === b[bestj - 1]) {
                besti--;
                bestj--;
                bestsize++;
            }

            while (besti + bestsize < ahi && bestj + bestsize < bhi && !isbjunk(b[bestj + bestsize]) &&
            a[besti + bestsize] === b[bestj + bestsize]) {
                bestsize++;
            }

            while (besti > alo && bestj > blo && isbjunk(b[bestj - 1]) && a[besti - 1] === b[bestj - 1]) {
                besti--;
                bestj--;
                bestsize++;
            }

            while (besti + bestsize < ahi && bestj + bestsize < bhi && isbjunk(b[bestj + bestsize]) &&
            a[besti + bestsize] === b[bestj + bestsize]) {
                bestsize++;
            }

            return [besti, bestj, bestsize];
        }

        this.get_matching_blocks = function () {
            if (this.matching_blocks != null) return this.matching_blocks;
            var la = this.a.length;
            var lb = this.b.length;

            var queue = [[0, la, 0, lb]];
            var matchingBlocks = [];
            var alo, ahi, blo, bhi, qi, i, j, k, x;
            while (queue.length) {
                qi = queue.pop();
                alo = qi[0];
                ahi = qi[1];
                blo = qi[2];
                bhi = qi[3];
                x = this.find_longest_match(alo, ahi, blo, bhi);
                i = x[0];
                j = x[1];
                k = x[2];

                if (k) {
                    matchingBlocks.push(x)
                    if (alo < i && blo < j) {
                        queue.push([alo, i, blo, j])
                    }
                    if (i + k < ahi && j + k < bhi) {
                        queue.push([i + k, ahi, j + k, bhi])
                    }
                }
            }

            matchingBlocks.sort(compareTuple)

            var i1 = 0;
            var j1 = 0;
            var k1 = 0;
            var block = 0;
            var i2;
            var j2;
            var k2;
            var nonAdjacent = [];
            for (var idx in matchingBlocks) {
                if (matchingBlocks.hasOwnProperty(idx)) {
                    block = matchingBlocks[idx]
                    i2 = block[0]
                    j2 = block[1]
                    k2 = block[2]
                    if (i1 + k1 === i2 && j1 + k1 === j2) {
                        k1 += k2
                    } else {
                        if (k1) nonAdjacent.push([i1, j1, k1])
                        i1 = i2
                        j1 = j2
                        k1 = k2
                    }
                }
            }

            if (k1) nonAdjacent.push([i1, j1, k1])

            nonAdjacent.push([la, lb, 0])
            this.matching_blocks = nonAdjacent
            return this.matching_blocks
        };

        this.get_opcodes = function () {
            if (this.opcodes != null) return this.opcodes
            var i = 0
            var j = 0
            var answer = []
            this.opcodes = answer
            var block, ai, bj, size, tag
            var blocks = this.get_matching_blocks()
            for (var idx in blocks) {
                if (blocks.hasOwnProperty(idx)) {
                    block = blocks[idx]
                    ai = block[0]
                    bj = block[1]
                    size = block[2]
                    tag = ''
                    if (i < ai && j < bj) {
                        tag = 'replace'
                    } else if (i < ai) {
                        tag = 'delete'
                    } else if (j < bj) {
                        tag = 'insert'
                    }
                    if (tag) answer.push([tag, i, ai, j, bj]);
                    i = ai + size;
                    j = bj + size;

                    if (size) answer.push(['equal', ai, i, bj, j]);
                }
            }

            return answer;
        }

        this.ratio = function () {
            var matches = fnReduce(
                function (sum, triple) {
                    return sum + triple[triple.length - 1]
                },
                this.get_matching_blocks(), 0
            )
            return calcRatio(matches, this.a.length + this.b.length)
        }

        this.isjunk = isJunk || fnIsJunk
        this.a = this.b = null
        this.set_seqs(a, b)
    };

    var buildInlineRow = function (bIndex, nIndex, textLines, change) {
        var row = {}
        row.b = bIndex === null ? '' : '' + (bIndex + 1);
        row.n = nIndex === null ? '' : '' + (nIndex + 1);
        row.ns = change;
        row.nt = textLines[bIndex != null ? bIndex : nIndex].replace(/\t/g, '\u00a0\u00a0\u00a0\u00a0');
        return row;
    };

    var buildSideBySideRow = function (bIndex, bEnd, bTextLines, bChange, nIndex, nEnd, nTextLines, nChange) {
        if (bChange === 'replace') {
            bChange = 'delete';
            nChange = 'insert';
        }
        var row = {}
        if (bIndex < bEnd) {
            row.b = bIndex + 1;
            row.bt = bTextLines[bIndex].replace(/\t/g, '\u00a0\u00a0\u00a0\u00a0');
            row.bs = bChange;
        } else {
            row.b = '';
            row.bt = '';
            row.bs = 'empty';
        }

        if (nIndex < nEnd) {
            row.n = nIndex + 1;
            row.nt = nTextLines[nIndex].replace(/\t/g, '\u00a0\u00a0\u00a0\u00a0');
            row.ns = nChange;
        } else {
            row.n = '';
            row.nt = '';
            row.ns = 'empty';
        }

        return row
    }

    var buildDiffRows = function (baseTextLines, newTextLines, opcodes, contextSize, inline) {
        if (baseTextLines === null) {
            throw Error('Cannot build diff view; baseTextLines is not defined.')
        }
        if (newTextLines === null) {
            throw Error('Cannot build diff view; newTextLines is not defined.')
        }
        if (!opcodes) {
            throw Error('Cannot build diff view; opcodes is not defined.')
        }

        var diffRows = []

        for (var idx = 0; idx < opcodes.length; idx++) {
            var code = opcodes[idx];
            var change = code[0];
            var b = code[1];
            var be = code[2];
            var n = code[3];
            var ne = code[4];
            var rowCount = Math.max(be - b, ne - n);
            var topRows = [];
            var bottomRows = [];
            var node = {};
            for (var i = 0; i < rowCount; i++) {
                if (contextSize && opcodes.length > 1 && ((idx > 0 && i === contextSize) || (idx === 0 && i === 0)) && change === 'equal') {
                    var jump = rowCount - ((idx === 0 ? 1 : 2) * contextSize);
                    if (jump > 1) {
                        node = {b: '...', n: '...'};
                        b += jump;
                        n += jump;
                        i += jump - 1;

                        if (!inline) {
                            node.bt = '';
                            node.bs = 'skip';
                        }
                        node.nt = '';
                        node.ns = 'skip';
                        topRows.push(node);
                        // skip last lines if they're all equal
                        if (idx + 1 === opcodes.length) {
                            break;
                        } else {
                            continue;
                        }
                    }
                }

                if (inline) {
                    if (change === 'insert') {
                        topRows.push(buildInlineRow(null, n++, newTextLines, change));
                    } else if (change === 'replace') {
                        if (b < be) {
                            topRows.push(buildInlineRow(b++, null, baseTextLines, 'delete'));
                        }
                        if (n < ne) {
                            bottomRows.push(buildInlineRow(null, n++, newTextLines, 'insert'));
                        }
                    } else if (change === 'delete') {
                        topRows.push(buildInlineRow(b++, null, baseTextLines, change));
                    } else {
                        // equal
                        topRows.push(buildInlineRow(b++, n++, baseTextLines, change));
                    }
                } else {
                    topRows.push(buildSideBySideRow(b, be, baseTextLines, change, n, ne, newTextLines, change));
                    b = b < be ? b + 1 : b;
                    n = n < ne ? n + 1 : n;
                }
            }
            topRows.forEach(function (r) {
                return diffRows.push(r)
            })
            bottomRows.forEach(function (r) {
                return diffRows.push(r)
            })
        }

        return diffRows
    };

    var diff = function (baseText, newText, contextSize, isInline) {
        var baseLines = stringAsLines(baseText)
        var newLines = stringAsLines(newText)
        var sm = new SequenceMatcher(baseLines, newLines)

        var opCodes = sm.get_opcodes()
        return buildDiffRows(baseLines, newLines, opCodes, contextSize, isInline)
    };
    return {
        restrict: 'AE',
        scope: {
            config: '=config',
        },
        templateUrl: 'views/v2/diff.html',
        controller: function ($scope) {
            this.leftTitle = $scope.config.leftTitle||"Base";
            this.rightTitle = $scope.config.rightTitle||"Target";
            this.content = diff($scope.config.leftData, $scope.config.rightData, 5, false);
        },
        controllerAs: 'ctrl'
    };
});