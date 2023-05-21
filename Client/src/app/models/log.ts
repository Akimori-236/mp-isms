export interface Log {
    store_id: string
    time:string
    activity: string
    email: string
    instrument_id: string
    instrument_type: string
    serial_number: string
    isRepairing: boolean
    remarks?: string
}