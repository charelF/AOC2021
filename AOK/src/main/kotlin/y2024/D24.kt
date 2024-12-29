package y2024

import java.io.File
import extensions.*
import kotlin.system.measureTimeMillis

class D24 {
    val parts = File("../i24/24").readText().split("\n\n")
    val known = parts.first().lines().associate {
        it.split(": ")
            .let { (wire, value) -> wire to (value == "1") }
    }.toMutableMap()
    val gates = parts.last().lines().map {
        it.split(" ").let { (w1, op, w2, _, w3) ->
            when (op) {
                "XOR" -> Gate(w1, w2, op, Boolean::xor, w3)
                "AND" -> Gate(w1, w2, op, Boolean::and, w3)
                "OR" -> Gate(w1, w2, op, Boolean::or, w3)
                else -> TODO()
            }
        }
    }

    data class Gate (
        val lhs: String,
        val rhs: String,
        val op: String,
        val exec: (Boolean, Boolean) -> Boolean,
        val res: String,
    )

    fun f1() {
        var prev = 0
        var diff: Int = known.size - prev
        while (diff != 0) {
            for (gate in gates) {
                if (gate.lhs in known && gate.rhs in known) {
                    known[gate.res] = gate.exec(known[gate.lhs]!!, known[gate.rhs]!!)
                }
            }
            diff = known.size - prev
            prev = known.size
        }
        known.filterKeys { it.startsWith('z') }
            .toSortedMap().reversed().values.map { it.toInt() }
            .joinToString("")
            .toLong(2)
            .print()
        println(45213383376616)
    }

    enum class TSMark { NONE, TEMP, PERM }
    data class TSNode (
        val gate: Gate,
        var mark: TSMark
    ) {
        fun getDescendants(network: List<TSNode>): List<TSNode> {
            // find all nodes X where there is an edge from this to X
           return network.filter { it.gate.lhs == gate.res || it.gate.rhs == gate.res }
        }
    }

    /**
     * using the DFS-based algo from https://en.wikipedia.org/wiki/Topological_sorting
     */
    fun toposort(gates: List<Gate>): List<Gate> {
        val nodes = gates.map { TSNode(it, TSMark.NONE) }
        val l: MutableList<TSNode> = mutableListOf()
        val unmarked = {node: TSNode -> node.mark == TSMark.NONE  }

        fun visit(node: TSNode) {
            if (node.mark == TSMark.PERM) return
            if (node.mark == TSMark.TEMP) throw Exception("graph has cycle; cant do toposort")
            node.mark = TSMark.TEMP
            // for each node m with an edge from n to m, visit m
            node.getDescendants(nodes).forEach { visit(it) }
            node.mark = TSMark.PERM
            l.addFirst(node)
        }

        // while exists nodes without a permanent mark do
        while (nodes.any(unmarked)) {
            val node = nodes.first(unmarked)
            visit(node)
        }
        return l.map {it.gate}
    }

    fun generator() {
        val sortedGates = toposort(gates)

        fun makeIndexer(s: String): String {
            if (s.startsWith('x') || s.startsWith('y') || s.startsWith('z')) {
                return "${s.first()}[${s.drop(1).toInt()}]"
            }
            return s
        }

        for (g in sortedGates) {
            val glhs = makeIndexer(g.lhs)
            val grhs = makeIndexer(g.rhs)
            val gres = makeIndexer(g.res)
            val start = if ('[' in gres) gres else "val $gres"
            val strOp = when(g.op) {
                "AND" -> "&&"
                "OR" -> "||"
                "XOR" -> "xor"
                else -> TODO()
            }
            println("$start = $glhs $strOp $grhs")
        }
    }

    fun generated(x: BooleanArray, y: BooleanArray, z: BooleanArray) {
        val fcn = y[34] xor x[34]
        val kqw = y[27] xor x[27]
        val gmr = y[12] xor x[12]
        val gcr = y[20] xor x[20]
        val nbj = x[17] && y[17]
        val wqk = y[19] xor x[19]
        val vss = y[44] && x[44]
        val qjg = y[3] && x[3]
        val hds = x[34] && y[34]
        val ggq = x[42] && y[42]
        val jcp = x[20] && y[20]
        val dgc = x[21] && y[21]
        val trt = x[5] xor y[5]
        val tdh = x[19] && y[19]
        val kjv = y[36] && x[36]
        val tkv = y[41] && x[41]
        val fcs = y[16] && x[16]
        val tvc = x[43] && y[43]
        val jmh = y[13] && x[13]
        z[0] = y[0] xor x[0]
        val spf = x[30] && y[30]
        val gfr = y[32] && x[32]
        val wrc = x[2] && y[2]
        val wmw = x[2] xor y[2]
        val njd = x[44] xor y[44]
        val ttj = x[35] xor y[35]
        val dht = x[10] xor y[10]
        val gbf = y[8] && x[8]
        val vtp = y[37] && x[37]
        val sws = y[22] xor x[22]
        val rkm = x[14] xor y[14]
        val khq = y[23] xor x[23]
        val bgb = x[1] xor y[1]
        val jrh = y[8] xor x[8]
        val mgb = x[39] && y[39]
        val qdh = y[26] && x[26]
        val cnk = x[9] && y[9]
        val hkm = x[6] xor y[6]
        val brk = y[7] && x[7]
        val jbf = x[16] xor y[16]
        val wph = x[38] xor y[38]
        val bhb = x[28] xor y[28]
        val bcg = y[24] && x[24]
        val dnt = x[30] xor y[30]
        val fsv = y[12] && x[12]
        val trn = y[39] xor x[39]
        val qwf = x[9] xor y[9]
        val tmc = y[17] xor x[17]
        val jgq = y[27] && x[27]
        val wns = y[28] && x[28]
        val vnq = y[23] && x[23]
        val tbk = x[3] xor y[3]
        val kfk = y[18] xor x[18]
        val qgp = x[32] xor y[32]
        val frj = y[5] && x[5]
        val ftr = y[25] && x[25]
        val grj = x[40] && y[40]
        val pqr = x[38] && y[38]
        val qmn = x[4] && y[4]
        val fgw = x[0] && y[0]
        val gww = fgw && bgb
        z[1] = fgw xor bgb
        val fgs = y[29] && x[29]
        val gdv = x[25] xor y[25]
        val bqc = x[22] && y[22]
        val hdg = x[21] xor y[21]
        val rvm = x[15] && y[15]
        val kvb = y[40] xor x[40]
        val nnw = y[26] xor x[26]
        val qvt = y[35] && x[35]
        val ttm = x[33] && y[33]
        val dmq = x[13] xor y[13]
        val twr = y[33] xor x[33]
        val tjk = y[4] xor x[4]
        val pfd = x[11] && y[11]
        val pjh = y[1] && x[1]
        val jnw = y[15] xor x[15]
        val qgg = y[42] xor x[42]
        val nhn = y[41] xor x[41]
        val kkb = y[24] xor x[24]
        z[14] = y[14] && x[14]
        val wwp = pjh || gww
        z[2] = wmw xor wwp
        val dng = wwp && wmw
        val vmk = dng || wrc
        val vph = vmk && tbk
        val cpn = qjg || vph
        z[4] = cpn xor tjk
        val tvf = tjk && cpn
        val tfr = tvf || qmn
        val ncs = tfr && trt
        z[5] = tfr xor trt
        val tsr = x[29] xor y[29]
        val vvc = ncs || frj
        val wgp = vvc && hkm
        z[6] = vvc xor hkm
        val bjv = x[7] xor y[7]
        val chc = y[43] xor x[43]
        val fwf = y[11] xor x[11]
        val hvv = y[31] xor x[31]
        val rtc = x[37] xor y[37]
        val pvw = x[18] && y[18]
        val nqn = x[31] && y[31]
        z[3] = tbk xor vmk
        val kfb = x[36] xor y[36]
        val gdd = x[10] && y[10]
        val pqj = y[6] && x[6]
        val gmp = pqj || wgp
        val fjj = bjv && gmp
        val krg = brk || fjj
        val gnt = jrh && krg
        val hhp = gbf || gnt
        val jsd = cnk && hhp
        val cnq = jsd || qwf
        z[10] = dht xor cnq
        val mjm = cnq && dht
        val ptm = mjm || gdd
        z[11] = fwf xor ptm
        val mpd = fwf && ptm
        val gfj = mpd || pfd
        val bpv = gfj && gmr
        val jdr = bpv || fsv
        z[13] = dmq xor jdr
        val gfn = jdr && dmq
        val ndq = gfn || jmh
        val vhm = ndq xor rkm
        val fgv = rkm && ndq
        val bbw = vhm || fgv
        z[15] = bbw xor jnw
        val rrc = jnw && bbw
        val dhq = rvm || rrc
        z[16] = jbf xor dhq
        val npc = dhq && jbf
        val nmh = npc || fcs
        z[17] = nmh xor tmc
        val cpg = tmc && nmh
        val jbm = cpg || nbj
        z[18] = jbm xor kfk
        val bmt = jbm && kfk
        val srk = pvw || bmt
        val tkq = srk && wqk
        val pvt = tdh || tkq
        val pnv = pvt && gcr
        val rqb = pnv || jcp
        val rvh = rqb && hdg
        val jmq = rvh || dgc
        val bkw = jmq && sws
        val fww = bqc || bkw
        z[23] = khq xor fww
        val vmj = fww && khq
        val rwd = vmj || vnq
        z[24] = kkb xor rwd
        val fnj = kkb && rwd
        val bdb = fnj || bcg
        z[25] = gdv xor bdb
        val cfj = gdv && bdb
        val htg = cfj || ftr
        z[26] = nnw xor htg
        val mpb = nnw && htg
        val kqj = mpb || qdh
        val snv = kqw && kqj
        z[27] = snv || jgq
        val mps = kqw xor kqj
        val kcq = bhb && mps
        val fhw = wns || kcq
        z[29] = tsr xor fhw
        val wfp = tsr && fhw
        val dgt = fgs || wfp
        z[30] = dgt xor dnt
        val whw = dgt && dnt
        val ctm = spf || whw
        val vdn = hvv && ctm
        val tnb = vdn || nqn
        z[32] = qgp xor tnb
        val bnt = tnb && qgp
        val vck = gfr || bnt
        z[33] = twr xor vck
        val nnr = twr && vck
        val kvk = nnr || ttm
        val jdk = fcn && kvk
        val dpc = hds || jdk
        z[35] = ttj xor dpc
        val dsk = dpc && ttj
        val cgk = dsk || qvt
        z[36] = cgk xor kfb
        val ngh = cgk && kfb
        val rbs = kjv || ngh
        z[37] = rtc xor rbs
        val tcc = rbs && rtc
        val sfj = tcc || vtp
        val dsj = sfj && wph
        val gpm = pqr || dsj
        z[39] = trn && gpm
        val msq = gpm xor trn
        val cqt = mgb || msq
        z[40] = kvb xor cqt
        val mvg = cqt && kvb
        val wkr = grj || mvg
        z[41] = wkr xor nhn
        val dcs = nhn && wkr
        val bbb = dcs || tkv
        val bwg = qgg && bbb
        val kdp = bwg || ggq
        z[43] = kdp xor chc
        val jhf = chc && kdp
        val ndn = jhf || tvc
        val psd = njd && ndn
        z[45] = vss || psd
        z[44] = ndn xor njd
        z[42] = bbb xor qgg
        z[38] = sfj xor wph
        z[34] = fcn xor kvk
        z[31] = hvv xor ctm
        z[28] = bhb xor mps
        z[22] = sws xor jmq
        z[21] = rqb xor hdg
        z[20] = gcr xor pvt
        z[19] = wqk xor srk
        z[12] = gfj xor gmr
        z[9] = cnk xor hhp
        z[8] = jrh xor krg
        z[7] = gmp xor bjv
    }

    fun main() {
        f1()


    }

    fun x() {

    }
}

fun main() {
    D24().main()
}

