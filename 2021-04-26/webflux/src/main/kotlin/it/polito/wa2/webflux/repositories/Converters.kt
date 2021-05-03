package it.polito.wa2.webflux.repositories

import io.r2dbc.spi.Row
import it.polito.wa2.webflux.domain.Address
import it.polito.wa2.webflux.domain.Producer
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import org.springframework.data.r2dbc.mapping.OutboundRow
import org.springframework.r2dbc.core.Parameter

@ReadingConverter
class ProducerReader: Converter<Row, Producer> {

    override fun convert(r: Row) =
        Producer(
            (r.get("id") as Int?)?.toLong(),
            r.get("name") as String,
            Address(
                r.get("street") as String,
                r.get("zip") as String,
                r.get("city") as String
            )
        )

}

@WritingConverter
class ProducerWriter: Converter<Producer, OutboundRow> {
    override fun convert(p: Producer) = OutboundRow().apply{
        if (p.id != null) put("id", Parameter.from(p.id))
        put("name", Parameter.from(p.name))
        put("street", Parameter.from(p.address.street))
        put("zip", Parameter.from(p.address.zip))
        put("city", Parameter.from(p.address.city))
    }
}
