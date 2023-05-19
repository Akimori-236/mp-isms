import { User } from "./user"

export interface Instrument {
    instrument_id: string
    instrument_type: string
    brand: string
    model: string
    serial_number: string
    store_id: string
    store_name: string
    isRepairing: boolean
    remarks: string
    // loaned out to
    email?: string
    givenname?: string
    familyname?: string
}